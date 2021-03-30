package ch.kanti.nesa.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.databinding.ActivitySplashBinding;

import ch.kanti.nesa.objects.SubjectsAndGrades;
import ch.kanti.nesa.scrapers.ContentScrapers;
import ch.kanti.nesa.scrapers.DocumentScraper;
import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Grades;
import ch.kanti.nesa.tables.Subjects;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    public static int SPLASH_TIME_OUT = 1500;
    public static String LOGIN_FORM_URL = "https://ksw.nesa-sg.ch/loginto.php?mode=0&lang=";
    public static String ACTION_URL = "https://ksw.nesa-sg.ch/index.php?pageid=";
    public static String username, password;
    public static Document mainPage, markPage, absencesPage, bankPage, emailPage;

    ViewModel viewModel;

    @SuppressLint("CommitPrefEdits")
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

        if(!loginComplete){
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent loginActivity = new Intent(context, LoginActivity.class);
                startActivity(loginActivity);
                finish();
            }, SPLASH_TIME_OUT);
        } else {
            if(App.isDeviceOnline()){
                username = App.sharedPreferences.getString("username", "");
                password = App.sharedPreferences.getString("password","");
                if (firstLogin) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.hintText.setVisibility(View.VISIBLE);
                    getPages();
                    initializeScraping();
                    App.editor.putBoolean(App.FIRST_LOGIN, false);
                    App.editor.commit();
                }
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent mainActivity = new Intent(this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                }, SPLASH_TIME_OUT);
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