package ch.kanti.nesa.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.background.DeviceOnline;
import ch.kanti.nesa.databinding.ActivityMainBinding;
import ch.kanti.nesa.fragments.AbsencesFragment;
import ch.kanti.nesa.fragments.BankFragment;
import ch.kanti.nesa.fragments.GradesFragment;
import ch.kanti.nesa.fragments.HomeFragment;
import ch.kanti.nesa.fragments.SettingsFragment;
import ch.kanti.nesa.fragments.StudentsFragment;
import ch.kanti.nesa.fragments.SubjectsFragment;
import ch.kanti.nesa.fragments.TimetableDayFragment;
import ch.kanti.nesa.objects.LoginAndScrape;
import ch.kanti.nesa.scrapers.Network;

public class MainActivity extends AppCompatActivity implements HomeFragment.HomeFragmentShortcut {

    public ActivityMainBinding binding;
    public static String username;
    public static String password;
    public static ViewModel viewModel;

    public static final int SHORTCUT_BANK = 1;
    public static final int SHORTCUT_GRADES = 2;
    public static final int SHORTCUT_ABSENCE = 3;
    public static final int SHORTCUT_TIMETABLE = 4;
    public static final int SHORTCUT_STUDENTS = 5;

    ExecutorService executor = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int theme = App.sharedPreferences.getInt("theme", 0);
        int border = App.sharedPreferences.getInt("border", 0);

        if (theme == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (theme == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (theme == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        if(border == 0) {
            setTheme(R.style.Theme_NESA);
        } else if (border == 1) {
            setTheme(R.style.Theme_NESA_OLED);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        boolean firstLogin = App.sharedPreferences.getBoolean(App.FIRST_LOGIN, true);

        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.setOnItemSelectedListener(navListener);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ViewModel.class);

        username = App.sharedPreferences.getString("username", "");
        password = App.sharedPreferences.getString("password", "");

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setColorSchemeColors(getColor(R.color.primaryColor));
            executor.execute(this::syncData);
        });

        if(!firstLogin) {
            executor.execute(this::syncData);
        }

        if(firstLogin) {
            App.sharedPreferences.edit().putBoolean(App.FIRST_LOGIN, false).apply();
        }

        Intent intent = getIntent();
        launchNotification(intent);
    }

    @SuppressLint("NonConstantResourceId")
    public final NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_home:
                selectedFragment = new HomeFragment();
                break;
            case R.id.nav_grades:
                selectedFragment = new SubjectsFragment();
                break;
            case R.id.nav_timetable:
                selectedFragment = new TimetableDayFragment();
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

    private void syncData() {
        if(DeviceOnline.check()) {
            LoginAndScrape scrape = Network.checkLoginAndPages(true, false, true, false, username, password);
            if (scrape.isLoginCorrect()) {
                viewModel.insertSubjects(scrape.getSubjectsAndGrades().getSubjectList());
                viewModel.insertGrades(scrape.getSubjectsAndGrades().getGradeList());
                viewModel.insertBank(scrape.getBankStatements());
                viewModel.insertAbsences(scrape.getAbsences());
                viewModel.insertLessons(true, scrape.getLessons(), scrape.getExams());
            } else {
                runOnUiThread(()->{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle(R.string.login_button_label)
                            .setMessage(R.string.credentialsChanged)
                            .setPositiveButton(getString(R.string.dialogButtonOk), (dialog, which) -> {
                                App.sharedPreferences.edit().putString("username", "").apply();
                                App.sharedPreferences.edit().putString("password", "").apply();
                                App.sharedPreferences.edit().putBoolean(App.LOGIN_COMPLETED, false).apply();
                                App.sharedPreferences.edit().putBoolean(App.FIRST_LOGIN, true).apply();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                                viewModel.deleteAllBank();
                                viewModel.deleteAllAbsences();
                                viewModel.deleteAllSubjects();
                                viewModel.deleteAllGrades();
                                viewModel.deleteAllAccountInfo();
                                viewModel.deleteAllStudents();
                                viewModel.deleteAllLessons();
                            })
                            .setCancelable(false);
                    builder.show();
                });
            }
            binding.swipeRefresh.setRefreshing(false);
            runOnUiThread(()-> Toast.makeText(this, getString(R.string.synced), Toast.LENGTH_SHORT).show());
        } else {
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onShortcutClicked(int shortcut) {
        Fragment selectedFragment;
        int selectedIcon;
        String tag = "";
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
                selectedIcon = R.id.nav_home;
                tag = "ABSENCE_FRAGMENT";
                break;
            case SHORTCUT_TIMETABLE:
                selectedFragment = new TimetableDayFragment();
                selectedIcon = R.id.nav_timetable;
                break;
            case SHORTCUT_STUDENTS:
                selectedFragment = new StudentsFragment();
                selectedIcon = R.id.nav_home;
                tag = "STUDENTS_FRAGMENT";
                break;
            default:
                selectedFragment = new HomeFragment();
                selectedIcon = R.id.nav_home;
        }
        binding.bottomNavigation.setSelectedItemId(selectedIcon);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment, tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        GradesFragment gradesFragment = (GradesFragment) getSupportFragmentManager().findFragmentByTag("GRADES_FRAGMENT");
        StudentsFragment studentsFragment = (StudentsFragment) getSupportFragmentManager().findFragmentByTag("STUDENTS_FRAGMENT");
        AbsencesFragment absencesFragment = (AbsencesFragment) getSupportFragmentManager().findFragmentByTag("ABSENCE_FRAGMENT");
        if (gradesFragment != null && gradesFragment.isVisible()) {
            int subjectPosition = gradesFragment.getSubjectPosition();
            SubjectsFragment subjectsFragment = new SubjectsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", subjectPosition);
            subjectsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, subjectsFragment).commit();
            binding.bottomNavigation.setSelectedItemId(R.id.nav_grades);
        } else if (studentsFragment != null && studentsFragment.isVisible()) {
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else if (absencesFragment != null && absencesFragment.isVisible()) {
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else{
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
                binding.bottomNavigation.setSelectedItemId(R.id.nav_settings);
            } else if (intent.getIntExtra("type", -1) == 2) {
                BankFragment bankFragment = new BankFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, bankFragment).commit();
                binding.bottomNavigation.setSelectedItemId(R.id.nav_account);
            }
        }
    }
}