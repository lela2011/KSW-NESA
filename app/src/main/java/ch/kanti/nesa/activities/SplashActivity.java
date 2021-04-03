package ch.kanti.nesa.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.background.DeviceOnline;
import ch.kanti.nesa.databinding.ActivitySplashBinding;
import ch.kanti.nesa.objects.LoginAndScrape;
import ch.kanti.nesa.scrapers.Network;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    public static int SPLASH_TIME_OUT = 1500;
    public static String username, password;

    ViewModel viewModel;

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_NESA_OLED);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Context context = getApplicationContext();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ViewModel.class);

        boolean loginComplete = App.sharedPreferences.getBoolean(App.LOGIN_COMPLETED, false);
        boolean firstLogin = App.sharedPreferences.getBoolean(App.FIRST_LOGIN, true);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            if(!loginComplete){
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent loginActivity = new Intent(context, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }, SPLASH_TIME_OUT);
            } else {
                boolean online = DeviceOnline.check();
                if(online){
                    username = App.sharedPreferences.getString("username", "");
                    password = App.sharedPreferences.getString("password","");
                    if (firstLogin) {
                        runOnUiThread(()->{
                            binding.progressBar.setVisibility(View.VISIBLE);
                            binding.hintText.setVisibility(View.VISIBLE);
                        });
                        App.sharedPreferences.edit().putInt("colCol1", getColor(R.color.gold)).apply();
                        App.sharedPreferences.edit().putInt("colCol2", getColor(R.color.green)).apply();
                        App.sharedPreferences.edit().putInt("colCol3", getColor(R.color.orange)).apply();
                        App.sharedPreferences.edit().putInt("colCol4", getColor(R.color.red)).apply();
                        App.sharedPreferences.edit().putFloat("colRange1", 5.0f).apply();
                        App.sharedPreferences.edit().putFloat("colRange2", 4.0f).apply();

                        LoginAndScrape scrape = Network.checkLoginAndPages(true, true, username, password);
                        if (scrape.isLoginCorrect()) {
                            viewModel.insertInfo(scrape.getAccountInfos());
                            viewModel.insertSubjects(scrape.getSubjectsAndGrades().getSubjectList());
                            viewModel.insertGrades(scrape.getSubjectsAndGrades().getGradeList());
                            viewModel.insertBank(scrape.getBankStatements());
                            viewModel.insertAbsences(scrape.getAbsences());
                        }
                    }
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Intent mainActivity = new Intent(this, MainActivity.class);
                        startActivity(mainActivity);
                        finish();
                    }, SPLASH_TIME_OUT);
                } else {
                    Toast.makeText(this, getString(R.string.youre_offline), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}