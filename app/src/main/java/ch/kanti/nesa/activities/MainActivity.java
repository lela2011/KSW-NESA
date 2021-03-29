package ch.kanti.nesa.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.databinding.ActivityMainBinding;
import ch.kanti.nesa.fragments.AbsencesFragment;
import ch.kanti.nesa.fragments.BankFragment;
import ch.kanti.nesa.fragments.GradesFragment;
import ch.kanti.nesa.fragments.SubjectsFragment;
import ch.kanti.nesa.fragments.HomeFragment;
import ch.kanti.nesa.fragments.SettingsFragment;
import ch.kanti.nesa.objects.SubjectsAndGrades;
import ch.kanti.nesa.scrapers.ContentScrapers;
import ch.kanti.nesa.scrapers.DocumentScraper;
import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Grades;
import ch.kanti.nesa.tables.Subjects;

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
    public static final int GRADES_FRAGMENT = 1;
    public static final int HOME_FRAGMENT = 0;
    public static final int ABSENCES_FRAGMENT = 2;
    public static final int BANK_FRAGMENT = 3;
    public static final int SETTINGS_FRAGMENT = 4;

    public int currentFragment = HOME_FRAGMENT;

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
        firstLogin = SplashActivity.sharedPreferences.getBoolean(SplashActivity.FIRST_LOGIN, false);

        if(firstLogin) {
            mainPage = SplashActivity.mainPage;
            markPage = SplashActivity.markPage;
            absencesPage = SplashActivity.absencesPage;
            bankPage = SplashActivity.bankPage;
            emailPage = SplashActivity.emailPage;
            initializeScraping();
        }
        username = SplashActivity.username;
        password = SplashActivity.password;

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefresh.setColorSchemeColors(getColor(R.color.primaryColor));
                executor.execute(() -> {
                    // TODO: Activate syncData for release
                    //syncData();
                });
            }
        });

        if(!firstLogin){
            //executor.execute(this::syncData);
            // TODO: Activate syncData for release
        }

        Intent intent = getIntent();
        lauchSubject(intent);
    }

    @SuppressLint("NonConstantResourceId")
    public final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_home:
                selectedFragment = new HomeFragment();
                currentFragment = HOME_FRAGMENT;
                break;
            case R.id.nav_grades:
                selectedFragment = new SubjectsFragment();
                currentFragment = GRADES_FRAGMENT;
                break;
            case R.id.nav_absences:
                selectedFragment = new AbsencesFragment();
                currentFragment = ABSENCES_FRAGMENT;
                break;
            case R.id.nav_account:
                selectedFragment = new BankFragment();
                currentFragment = BANK_FRAGMENT;
                break;
            case R.id.nav_settings:
                selectedFragment = new SettingsFragment();
                currentFragment = SETTINGS_FRAGMENT;
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

    private void scrapeMain() {
        ArrayList<AccountInfo> info = ContentScrapers.scrapeMain(mainPage);
        info.addAll(ContentScrapers.scrapeEmail(emailPage));
        viewModel.insertInfo(info);
    }

    private void scrapeMarks() {
        SubjectsAndGrades subjectsAndGrades = ContentScrapers.scrapeMarks(markPage);
        ArrayList<Grades> grades = subjectsAndGrades.gradesList;
        ArrayList<Subjects> subjects = subjectsAndGrades.subjectsList;

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

    private void syncData() {
        if(SplashActivity.isDeviceOnline()) {
            //mainPage = DocumentScraper.getMainPage();
            markPage = DocumentScraper.getMarkPage();
            absencesPage = DocumentScraper.getAbsencesPage();
            bankPage = DocumentScraper.getBankPage();
            emailPage = DocumentScraper.getEmailPage();
            initializeScraping();
            binding.swipeRefresh.setRefreshing(false);
            runOnUiThread(()->{
                Toast.makeText(this, getString(R.string.synced), Toast.LENGTH_SHORT).show();
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
                currentFragment = BANK_FRAGMENT;
                break;
            case SHORTCUT_GRADES:
                selectedFragment = new SubjectsFragment();
                selectedIcon = R.id.nav_grades;
                currentFragment = GRADES_FRAGMENT;
                break;
            case SHORTCUT_ABSENCE:
                selectedFragment = new AbsencesFragment();
                selectedIcon = R.id.nav_absences;
                currentFragment = ABSENCES_FRAGMENT;
                break;
            default:
                selectedFragment = new HomeFragment();
                selectedIcon = R.id.nav_home;
                currentFragment = HOME_FRAGMENT;
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
        lauchSubject(intent);
    }

    private void lauchSubject(Intent intent) {
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