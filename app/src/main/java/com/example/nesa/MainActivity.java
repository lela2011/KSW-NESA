package com.example.nesa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nesa.databinding.ActivityMainBinding;
import com.example.nesa.fragments.AbsencesFragment;
import com.example.nesa.fragments.AccountFragment;
import com.example.nesa.fragments.GradesFragment;
import com.example.nesa.fragments.HomeFragment;
import com.example.nesa.fragments.SettingsFragment;
import com.example.nesa.scrapers.ContentScrapers;
import com.example.nesa.scrapers.DocumentScraper;
import com.example.nesa.tables.AccountInfo;
import com.example.nesa.tables.BankStatement;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    public static String username;
    public static String password;
    public static ViewModel viewModel;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                selectedFragment = new HomeFragment();
                break;
            case R.id.nav_grades:
                selectedFragment = new GradesFragment();
                break;
            case R.id.nav_absences:
                selectedFragment = new AbsencesFragment();
                break;
            case R.id.nav_account:
                selectedFragment = new AccountFragment();
                break;
            case R.id.nav_settings:
                selectedFragment = new SettingsFragment();
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
    }

    private void scrapeMain() {
        ArrayList<AccountInfo> info = ContentScrapers.scrapeMain(mainPage);
        info.addAll(ContentScrapers.scrapeEmail(emailPage));
        viewModel.insertInfo(info);
    }

    private void scrapeMarks() {

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
}