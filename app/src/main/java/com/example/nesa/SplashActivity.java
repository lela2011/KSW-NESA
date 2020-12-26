package com.example.nesa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.nesa.databinding.ActivitySplashBinding;
import com.example.nesa.scrapers.CookieAndAuthScraper;
import com.example.nesa.scrapers.DocumentScraper;
import com.example.nesa.scrapers.PageScraper;
import com.example.nesa.tables.User;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
    public static final String LOGIN_COMPLETED = "IS_LOGIN_COMPLETE";
    public static final String FIRST_LOGIN = "FIRST_LOGIN";
    public static final int SPLASH_TIME_OUT = 1500;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static String usernameKey = "eThWmZq4t7w!z%C*F-J@NcRfUjXn2r5u";;
    public static String passwordKey = "C*F-JaNdRgUjXn2r5u8x/A?D(G+KbPeS";
    public static String LOGIN_FORM_URL = "https://ksw.nesa-sg.ch/loginto.php?mode=0&lang=";
    public static String ACTION_URL = "https://ksw.nesa-sg.ch/index.php?pageid=";
    public static String username, password;
    public static Document mainPage, markPage, absencesPage, bankPage;

    public static boolean netWorkAvailable = false;

    ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Context context = SplashActivity.this;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ViewModel.class);

        boolean loginComplete = sharedPreferences.getBoolean(LOGIN_COMPLETED, false);
        boolean firstLogin = sharedPreferences.getBoolean(FIRST_LOGIN, true);

        if(!loginComplete){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent loginActivity = new Intent(context, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            if(isDeviceOnline()){
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.hintText.setVisibility(View.VISIBLE);
                viewModel.getCredentials().observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if(user != null){
                            username = user.getUsername();
                            password = user.getPassword();
                            getPages();
                            Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(mainActivity);
                            finish();
                        }
                    }

                });
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(mainActivity);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }
        }
    }

    private void getPages() {
        mainPage = DocumentScraper.getMainPage();
        markPage = DocumentScraper.getMarkPage();
        absencesPage = DocumentScraper.getAbsencesPage();
        bankPage = DocumentScraper.getBankPage();
    }

    public static boolean isDeviceOnline() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> onlineFuture = executor.submit(new isDeviceOnlineFuture());
        try {
            return onlineFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

}