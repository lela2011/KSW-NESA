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
import com.example.nesa.tables.BankStatement;
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
    public static String username;
    public static String password;
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    public static ViewModel viewModel;

    List<AccountInfo> oldInfo = new ArrayList<>();

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

        viewModel.getCredentials().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user != null){
                    username = user.getUsername();
                    password = user.getPassword();
                    scrapeMain();
                    scrapeAccount();
                } else {
                }
            }
        });
    }

    private Document scrapePage (String url, HashMap<String, String> cookies, HashMap<String, String> formData) {
        Future<Document> pageFuture = executorService.submit(new PageScraper(cookies, formData, url));
        try {
            Document page = pageFuture.get();
            return page;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scrapeMain() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        Document mainPage = scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=1", cookies, formData);

        ArrayList<AccountInfo> info = Scrapers.scrapeMain(mainPage);
        viewModel.getAccountInfo().observe(this, new Observer<List<AccountInfo>>() {
            @Override
            public void onChanged(List<AccountInfo> accountInfo) {
                if (accountInfo.size() == 8) {
                    List<AccountInfo> infoList = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        int id = accountInfo.get(i).getId();
                        AccountInfo updatedEntry = new AccountInfo(info.get(i).value, info.get(i).order);
                        updatedEntry.setId(id);
                        infoList.add(updatedEntry);
                    }
                    if(!compareLists(oldInfo, infoList)){
                        viewModel.updateInfo(infoList);
                        oldInfo = infoList;
                    }
                } else if (accountInfo.size() == 0) {
                    List<AccountInfo> infoList = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        AccountInfo newEntry = new AccountInfo(info.get(i).value, info.get(i).order);
                        infoList.add(newEntry);
                    }
                    viewModel.insertInfo(infoList);
                }
            }
        });
    }

    private void scrapeMarks() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        Document accountPage = scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=21311", cookies, formData);
    }

    private void scrapeAbsences() {

    }

    private void scrapeAccount() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        Document accountPage = scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=21411", cookies, formData);

        ArrayList<BankStatement> debits = Scrapers.scrapeAccount(accountPage);
        Log.d("debits", debits.toString());
    }

    private CookieAndAuth cookiesAndAuth() {
        Future<CookieAndAuth> login = executorService.submit(new CookieAndAuthScraper());
        try {
            CookieAndAuth loginResponse = login.get();
            return loginResponse;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean compareLists(List<AccountInfo> oldInfo, List<AccountInfo> newInfo){
        if(oldInfo.size() == newInfo.size()){
            for(int i = 0; i<oldInfo.size(); i++){
                if(!(oldInfo.get(i).order == newInfo.get(i).order && oldInfo.get(i).value.equals(newInfo.get(i).value))){
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

}