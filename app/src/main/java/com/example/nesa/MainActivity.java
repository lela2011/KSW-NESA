package com.example.nesa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nesa.databinding.ActivityMainBinding;
import com.example.nesa.fragments.AbsencesFragment;
import com.example.nesa.fragments.BankFragment;
import com.example.nesa.fragments.GradesFragment;
import com.example.nesa.fragments.HomeFragment;
import com.example.nesa.fragments.SettingsFragment;
import com.example.nesa.scrapers.ContentScrapers;
import com.example.nesa.scrapers.DocumentScraper;
import com.example.nesa.tables.AccountInfo;
import com.example.nesa.tables.BankStatement;
import com.example.nesa.tables.Grades;
import com.example.nesa.tables.Subjects;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements HomeFragment.HomeFragmentShortcut {

    public ActivityMainBinding binding;
    public static String username;
    public static String password;
    public static ViewModel viewModel;

    HomeFragment homeFragment;
    GradesFragment gradesFragment;
    AbsencesFragment absencesFragment;
    BankFragment bankFragment;
    SettingsFragment settingsFragment;

    public static final int SHORTCUT_BANK = 1;
    public static final int SHORTCUT_GRADES = 2;

    ExecutorService executor = Executors.newFixedThreadPool(2);

    Document mainPage, markPage, absencesPage, bankPage, emailPage;

    boolean firstLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        homeFragment = new HomeFragment();
        gradesFragment = new GradesFragment();
        absencesFragment = new AbsencesFragment();
        bankFragment = new BankFragment();
        settingsFragment = new SettingsFragment();

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
                    syncData();
                });
            }
        });

        if(!firstLogin){
            executor.execute(()-> {
                syncData();
            });
        }
    }

    @SuppressLint("NonConstantResourceId")
    public final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_home:
                selectedFragment = homeFragment;
                break;
            case R.id.nav_grades:
                selectedFragment = gradesFragment;
                break;
            case R.id.nav_absences:
                selectedFragment = absencesFragment;
                break;
            case R.id.nav_account:
                selectedFragment = bankFragment;
                break;
            case R.id.nav_settings:
                selectedFragment = settingsFragment;
                break;

        }

        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();

        return true;
    };

    private void initializeScraping() {
        scrapeMain();
        scrapeBank();
        scrapeMarks();
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

    }

    private void scrapeBank() {
        ArrayList<BankStatement> debits = ContentScrapers.scrapeBank(bankPage);
        viewModel.insertBank(debits);
    }

    private void syncData() {
        if(SplashActivity.isDeviceOnline()) {
            mainPage = DocumentScraper.getMainPage();
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
                selectedFragment = bankFragment;
                selectedIcon = R.id.nav_account;
                break;
            case SHORTCUT_GRADES:
                selectedFragment = gradesFragment;
                selectedIcon = R.id.nav_grades;
                break;
            default:
                selectedFragment = homeFragment;
                selectedIcon = R.id.nav_home;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
        binding.bottomNavigation.setSelectedItemId(selectedIcon);
    }
}