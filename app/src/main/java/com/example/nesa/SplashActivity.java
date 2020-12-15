package com.example.nesa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
    public static final String LOGIN_COMPLETED = "IS_LOGIN_COMPLETE";
    public static final int SPLASH_TIME_OUT = 1500;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static String usernameKey;
    public static String passwordKey;
    public static String LOGIN_FORM_URL;
    public static String ACTION_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Context context = SplashActivity.this;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        usernameKey = "eThWmZq4t7w!z%C*F-J@NcRfUjXn2r5u";
        passwordKey = "C*F-JaNdRgUjXn2r5u8x/A?D(G+KbPeS";

        LOGIN_FORM_URL = "https://ksw.nesa-sg.ch/loginto.php?mode=0&lang=";
        ACTION_URL = "https://ksw.nesa-sg.ch/index.php?pageid=";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean loginComplete = sharedPreferences.getBoolean(LOGIN_COMPLETED, false);
                if(loginComplete){
                    Intent mainActivity = new Intent(context, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                } else{
                    Intent loginActivity = new Intent(context, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}