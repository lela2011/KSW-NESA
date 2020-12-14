    package com.example.nesa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.nesa.databinding.ActivityMainBinding;
import com.example.nesa.fragments.AbsencesFragment;
import com.example.nesa.fragments.AccountFragment;
import com.example.nesa.fragments.GradesFragment;
import com.example.nesa.fragments.HomeFragment;
import com.example.nesa.fragments.SettingsFragment;
import com.example.nesa.scrapers.CookieAndAuthScraper;
import com.example.nesa.scrapers.PageScraper;
import com.example.nesa.scrapers.Scrapers;
import com.example.nesa.tables.AccountInfo;
import com.example.nesa.tables.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

    public class MainActivity extends AppCompatActivity {

    public static final int PAGE_MAIN = 1;
    public static final int PAGE_MARKS = 2;
    public static final int PAGE_ABSENCES = 3;
    public static final int PAGE_ACCOUNT = 4;

    ActivityMainBinding binding;
    public static HashMap<String, String> cookies;
    public static HashMap<String, String> formData = new HashMap<>();
    public static String authToken;
    public static String username;
    public static String password;
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    public static ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ViewModel.class);

        initializeScraping();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
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

        }

        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();

        return true;
    };

    private void initializeScraping () {
        Future<CookieAndAuth> login = executorService.submit(new CookieAndAuthScraper());
        try {
            CookieAndAuth loginResponse = login.get();
            authToken = loginResponse.authToken;
            cookies = loginResponse.cookies;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        viewModel.getCredentials().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user != null){
                    username = user.getUsername();
                    password = user.getPassword();
                    formData.put("login", AES.decrypt(username, SplashActivity.usernameKey));
                    formData.put("passwort", AES.decrypt(password, SplashActivity.passwordKey));
                    formData.put("loginhash", authToken);
                    Log.d("debug", formData.toString());
                    scrapePage(SplashActivity.ACTION_URL, PAGE_MAIN);
                }
            }
        });
    }

    private void scrapePage (String url, int pageID) {
        Future<Document> pageFuture = executorService.submit(new PageScraper(cookies, formData, url));
        try {
            Document page = pageFuture.get();
            switch (pageID){
                case PAGE_MAIN: reScrapeMain(page);
                case PAGE_MARKS: reScrapeMarks(page);
                case PAGE_ABSENCES: reScrapeAbsences(page);
                case PAGE_ACCOUNT: reScrapeAccount(page);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void reScrapeMain(Document page) {
        ArrayList<AccountInfo> info = Scrapers.scrapeMain(page);
        viewModel.getAccountInfo().observe(this, new Observer<List<AccountInfo>>() {
            @Override
            public void onChanged(List<AccountInfo> accountInfos) {
                if (accountInfos.size() != 0 && info.size() != 0) {
                    for (int i = 0; i < 8; i++) {
                        int id = accountInfos.get(i).getId();
                        AccountInfo updatedEntry = new AccountInfo(info.get(i).value, info.get(i).order);
                        updatedEntry.setId(id);
                        viewModel.updateInfo(updatedEntry);
                    }
                } else {
                    for (int i = 0; i < 8; i++) {
                        AccountInfo newEntry = new AccountInfo(info.get(i).value, info.get(i).order);
                        viewModel.insertInfo(newEntry);
                    }
                }
            }
        });
    }

    private void reScrapeMarks(Document page) {
        Scrapers.scrapeMarks(page);
    }

    private void reScrapeAbsences(Document page) {
        Scrapers.scrapeAbsences(page);
    }

    private void reScrapeAccount(Document page) {
        Scrapers.scrapeAbsences(page);
    }

}