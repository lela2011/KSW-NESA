package com.example.nesa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
    public static final String LOGIN_COMPLETED = "IS_LOGIN_COMPLETE";
    public static final int SPLASH_TIME_OUT = 1500;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Context context = SplashActivity.this;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

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