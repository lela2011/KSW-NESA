package ch.kanti.nesa.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

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
import ch.kanti.nesa.tables.Grade;
import ch.kanti.nesa.tables.Subject;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    public static int SPLASH_TIME_OUT = 1500;
    public static final String LOGIN_FORM_URL = "https://ksw.nesa-sg.ch/loginto.php?mode=0&lang=";
    public static final String ACTION_URL = "https://ksw.nesa-sg.ch/index.php?pageid=";
    public static String username, password;
    public static Document mainPage, markPage, absencesPage, bankPage, emailPage;

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
                    App.sharedPreferences.edit().putInt("colCol1", getColor(R.color.gold)).commit();
                    App.sharedPreferences.edit().putInt("colCol2", getColor(R.color.green)).commit();
                    App.sharedPreferences.edit().putInt("colCol3", getColor(R.color.orange)).commit();
                    App.sharedPreferences.edit().putInt("colCol4", getColor(R.color.red)).commit();
                    App.sharedPreferences.edit().putFloat("colRange1", 5.0f).commit();
                    App.sharedPreferences.edit().putFloat("colRange2", 4.0f).commit();
                    runOnUiThread(()->{
                        binding.progressBar.setVisibility(View.VISIBLE);
                        binding.hintText.setVisibility(View.VISIBLE);
                    });
                    getPages();
                    initializeScraping();
                    App.sharedPreferences.edit().putBoolean(App.FIRST_LOGIN, false).commit();
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
        ArrayList<Grade> grades = subjectsAndGrades.gradeList;
        ArrayList<Subject> subjects = subjectsAndGrades.subjectList;

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