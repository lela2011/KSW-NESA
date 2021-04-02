package ch.kanti.nesa.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.background.LoginHandler;
import ch.kanti.nesa.databinding.ActivityMainBinding;
import ch.kanti.nesa.fragments.AbsencesFragment;
import ch.kanti.nesa.fragments.BankFragment;
import ch.kanti.nesa.fragments.GradesFragment;
import ch.kanti.nesa.fragments.SettingsFragment;
import ch.kanti.nesa.fragments.SubjectsFragment;
import ch.kanti.nesa.fragments.HomeFragment;
import ch.kanti.nesa.objects.SubjectsAndGrades;
import ch.kanti.nesa.scrapers.ContentScrapers;
import ch.kanti.nesa.scrapers.DocumentScraper;
import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Grade;
import ch.kanti.nesa.tables.Subject;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements HomeFragment.HomeFragmentShortcut {

    public ActivityMainBinding binding;
    public static String username;
    public static String password;
    public static ViewModel viewModel;

    public static final int SHORTCUT_BANK = 1;
    public static final int SHORTCUT_GRADES = 2;
    public static final int SHORTCUT_ABSENCE = 3;

    ExecutorService executor = Executors.newFixedThreadPool(2);

    Document mainPage, markPage, absencesPage, bankPage, emailPage;

    boolean firstLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_NESA_OLED);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ViewModel.class);
        firstLogin = App.sharedPreferences.getBoolean(App.FIRST_LOGIN, false);

        if(firstLogin) {
            mainPage = SplashActivity.mainPage;
            markPage = SplashActivity.markPage;
            absencesPage = SplashActivity.absencesPage;
            bankPage = SplashActivity.bankPage;
            emailPage = SplashActivity.emailPage;
            initializeScraping();
        }
        username = App.sharedPreferences.getString("username", "");
        password = App.sharedPreferences.getString("password", "");

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setColorSchemeColors(getColor(R.color.primaryColor));
            // TODO: Activate syncData for release
            executor.execute(this::syncData);
        });

        if(!firstLogin){
            executor.execute(this::syncData);
            // TODO: Activate syncData for release
        }

        Intent intent = getIntent();
        launchNotification(intent);
    }

    @SuppressLint("NonConstantResourceId")
    public final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_home:
                selectedFragment = new HomeFragment();
                break;
            case R.id.nav_grades:
                selectedFragment = new SubjectsFragment();
                break;
            case R.id.nav_absences:
                selectedFragment = new AbsencesFragment();
                break;
            case R.id.nav_account:
                selectedFragment = new BankFragment();
                break;
            case R.id.nav_settings:
                selectedFragment = new SettingsFragment();
                break;

        }

        assert selectedFragment != null;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();

        return true;
    };

    private void initializeScraping() {
        //scrapeMain();
        scrapeBank();
        scrapeMarks();
        scrapeAbsences();
    }

    /*private void scrapeMain() {
        ArrayList<AccountInfo> info = ContentScrapers.scrapeMain(mainPage);
        info.addAll(ContentScrapers.scrapeEmail(emailPage));
        viewModel.insertInfo(info);
    }*/

    private void scrapeMarks() {
        SubjectsAndGrades subjectsAndGrades = ContentScrapers.scrapeMarks(markPage);
        ArrayList<Grade> grades = subjectsAndGrades.gradeList;
        ArrayList<Subject> subjects = subjectsAndGrades.subjectList;

        viewModel.insertSubjects(subjects);
        viewModel.insertGrades(grades);
    }

    private void scrapeAbsences() {
        ArrayList<Absence> absences = ContentScrapers.scrapeAbsences(absencesPage);
        viewModel.insertAbsences(absences);
    }

    private void scrapeBank() {
        ArrayList<BankStatement> debits = ContentScrapers.scrapeBank(bankPage);
        viewModel.insertBank(debits);
    }

    @SuppressLint("ApplySharedPref")
    private void syncData() {
        //password = AES.encrypt("Lela&2011", App.passwordKey);
        if(App.isDeviceOnline() && LoginHandler.checkLoginCredentials(username, password) == App.LOGIN_SUCCESSFUL) {
            //mainPage = DocumentScraper.getMainPage();
            markPage = DocumentScraper.getMarkPage();
            absencesPage = DocumentScraper.getAbsencesPage();
            bankPage = DocumentScraper.getBankPage();
            emailPage = DocumentScraper.getEmailPage();
            initializeScraping();
            binding.swipeRefresh.setRefreshing(false);
            runOnUiThread(()-> Toast.makeText(this, getString(R.string.synced), Toast.LENGTH_SHORT).show());
        } else if (LoginHandler.checkLoginCredentials(username, password) == App.LOGIN_FAILED ||
                LoginHandler.checkLoginCredentials(username, password) == App.LOGIN_ERROR) {
            runOnUiThread(()->{
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Login")
                        .setMessage("Your username or password changed. You're about to be logged out")
                        .setPositiveButton(getString(R.string.dialogButtonOk), (dialog, which) -> {
                            App.sharedPreferences.edit().putString("username", "").commit();
                            App.sharedPreferences.edit().putString("password", "").commit();
                            App.sharedPreferences.edit().putBoolean(App.LOGIN_COMPLETED, false).commit();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                            viewModel.deleteAllBank();
                            viewModel.deleteAllAbsences();
                            viewModel.deleteAllSubjects();
                            viewModel.deleteAllGrades();
                            viewModel.deleteAllAccountInfo();
                        })
                        .setCancelable(false);
                builder.show();
            });
        } else {
            binding.swipeRefresh.setRefreshing(false);
        }
    }



    @Override
    public void onShortcutClicked(int shortcut) {
        Fragment selectedFragment;
        int selectedIcon;
        switch (shortcut){
            case SHORTCUT_BANK:
                selectedFragment = new BankFragment();
                selectedIcon = R.id.nav_account;
                break;
            case SHORTCUT_GRADES:
                selectedFragment = new SubjectsFragment();
                selectedIcon = R.id.nav_grades;
                break;
            case SHORTCUT_ABSENCE:
                selectedFragment = new AbsencesFragment();
                selectedIcon = R.id.nav_absences;
                break;
            default:
                selectedFragment = new HomeFragment();
                selectedIcon = R.id.nav_home;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
        binding.bottomNavigation.setSelectedItemId(selectedIcon);
    }

    @Override
    public void onBackPressed() {
        GradesFragment gradesFragment = (GradesFragment) getSupportFragmentManager().findFragmentByTag("GRADES_FRAGMENT");
        if (gradesFragment != null && gradesFragment.isVisible()) {
            int subjectPosition = gradesFragment.getSubjectPosition();
            SubjectsFragment subjectsFragment = new SubjectsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", subjectPosition);
            subjectsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, subjectsFragment).commit();
            binding.bottomNavigation.setSelectedItemId(R.id.nav_grades);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        launchNotification(intent);
    }

    private void launchNotification(Intent intent) {
        if(intent != null) {
            if(intent.getIntExtra("type", -1) == 0 ) {
                String subjectId = intent.getStringExtra("subjectID");
                float average = intent.getFloatExtra("average", -1.0f);
                float pluspoints = intent.getFloatExtra("pluspoints", -10.0f);

                Bundle bundle = new Bundle();
                bundle.putString("subject", subjectId);
                bundle.putFloat("average", average);
                bundle.putFloat("pluspoints", pluspoints);
                bundle.putInt("position", 0);
                GradesFragment newSubject = new GradesFragment();
                newSubject.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newSubject, "GRADES_FRAGMENT").commit();
            } else if (intent.getIntExtra("type", -1) == 1) {
                AbsencesFragment absencesFragment = new AbsencesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, absencesFragment).commit();
                binding.bottomNavigation.setSelectedItemId(R.id.nav_absences);
            } else if (intent.getIntExtra("type", -1) == 2) {
                BankFragment bankFragment = new BankFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, bankFragment).commit();
                binding.bottomNavigation.setSelectedItemId(R.id.nav_account);
            }
        }
    }
}