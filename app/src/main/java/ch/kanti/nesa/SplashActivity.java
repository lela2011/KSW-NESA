package ch.kanti.nesa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ch.kanti.nesa.databinding.ActivitySplashBinding;

import ch.kanti.nesa.futures.isDeviceOnlineFuture;
import ch.kanti.nesa.scrapers.ContentScrapers;
import ch.kanti.nesa.scrapers.DocumentScraper;
import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Grades;
import ch.kanti.nesa.tables.Subjects;
import ch.kanti.nesa.tables.User;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
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
    public static String usernameKey = "eThWmZq4t7w!z%C*F-J@NcRfUjXn2r5u";
    public static String passwordKey = "C*F-JaNdRgUjXn2r5u8x/A?D(G+KbPeS";
    public static String LOGIN_FORM_URL = "https://ksw.nesa-sg.ch/loginto.php?mode=0&lang=";
    public static String ACTION_URL = "https://ksw.nesa-sg.ch/index.php?pageid=";
    public static String username, password;
    public static Document mainPage, markPage, absencesPage, bankPage, emailPage;

    public static boolean netWorkAvailable = false;

    ViewModel viewModel;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_NESA_OLED);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Context context = SplashActivity.this;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ViewModel.class);

        boolean loginComplete = sharedPreferences.getBoolean(LOGIN_COMPLETED, false);
        boolean firstLogin = sharedPreferences.getBoolean(FIRST_LOGIN, true);

        if(!loginComplete){
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent loginActivity = new Intent(context, LoginActivity.class);
                startActivity(loginActivity);
                finish();
            }, SPLASH_TIME_OUT);
        } else {
            if(firstLogin){
                if(isDeviceOnline()){
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.hintText.setVisibility(View.VISIBLE);
                    viewModel.getCredentials().observe(this, user -> {
                        if(user != null){
                            username = user.getUsername();
                            password = user.getPassword();
                            getPages();
                            initializeScraping();
                            editor.putBoolean(FIRST_LOGIN, false);
                            editor.apply();
                            Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(mainActivity);
                            finish();
                        }
                    });
                }
            } else {
                viewModel.getCredentials().observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user != null) {
                            username = user.getUsername();
                            password = user.getPassword();

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(mainActivity);
                                finish();
                            }, SPLASH_TIME_OUT);
                        }
                    }
                });
            }
        }
    }

    private void getPages() {
        mainPage = DocumentScraper.getMainPage();
        markPage = DocumentScraper.getMarkPage();
        absencesPage = DocumentScraper.getAbsencesPage();
        bankPage = DocumentScraper.getBankPage();
        emailPage = DocumentScraper.getEmailPage();
    }

    public static boolean isDeviceOnline() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> onlineFuture = executor.submit(new isDeviceOnlineFuture());
        try {
            boolean deviceOnline = onlineFuture.get();
            netWorkAvailable = deviceOnline;
            return deviceOnline;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            netWorkAvailable = false;
            return false;
        }
    }

    private void initializeScraping() {
        scrapeMain();
        scrapeBank();
        scrapeMarks();
        scrapeAbsences();
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
        ArrayList<Absence> absences = ContentScrapers.scrapeAbsences(absencesPage);
        viewModel.insertAbsences(absences);
    }

    private void scrapeBank() {
        ArrayList<BankStatement> debits = ContentScrapers.scrapeBank(bankPage);
        viewModel.insertBank(debits);
    }

}