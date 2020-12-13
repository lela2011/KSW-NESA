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
import com.example.nesa.scrapers.MainScraper;
import com.example.nesa.tables.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

    public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    public static HashMap<String, String> cookies;
    public static HashMap<String, String> formData = new HashMap<>();
    public static String authToken;
    public static String username;
    public static String password;
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    ViewModel viewModel;

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
                username = user.getUsername();
                password = user.getPassword();
                formData.put("login", AES.decrypt(username, SplashActivity.usernameKey));
                formData.put("passwort", AES.decrypt(password, SplashActivity.passwordKey));
                formData.put("loginhash", authToken);
                Log.d("debug", formData.toString());
                scrapeName();
            }
        });
    }

    private void scrapeName () {
        Future<String> main = executorService.submit(new MainScraper(cookies, formData));
        try {
            String start = main.get();
            runOnUiThread(()-> Toast.makeText(this, start, Toast.LENGTH_SHORT).show());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}