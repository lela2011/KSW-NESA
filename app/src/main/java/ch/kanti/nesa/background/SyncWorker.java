package ch.kanti.nesa.background;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

import ch.kanti.nesa.AES;
import ch.kanti.nesa.AbsenceRepository;
import ch.kanti.nesa.App;
import ch.kanti.nesa.BankRepository;
import ch.kanti.nesa.GradesRepository;
import ch.kanti.nesa.activities.SplashActivity;
import ch.kanti.nesa.SubjectsRepository;
import ch.kanti.nesa.objects.SubjectsAndGrades;
import ch.kanti.nesa.scrapers.ContentScrapers;
import ch.kanti.nesa.scrapers.DocumentScraper;
import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.BankStatement;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        /*String username = App.sharedPreferences.getString("username","");
        String password = App.sharedPreferences.getString("password","");
        if(App.isDeviceOnline() && LoginHandler.checkLoginCredentials(username, password) == App.LOGIN_SUCCESSFUL) {
            Document gradesPage, absencesPage, bankPage;
            gradesPage = DocumentScraper.getMarkPage();
            absencesPage = DocumentScraper.getAbsencesPage();
            bankPage = DocumentScraper.getBankPage();

            ArrayList<Absence> absences = ContentScrapers.scrapeAbsences(absencesPage);
            SubjectsAndGrades subjectsAndGrades = ContentScrapers.scrapeMarks(gradesPage);
            ArrayList<BankStatement> bankStatements = ContentScrapers.scrapeBank(bankPage);

            Context context = getApplicationContext();

            GradesRepository gradesRepository = new GradesRepository(context);
            SubjectsRepository subjectsRepository = new SubjectsRepository(context);
            BankRepository bankRepository = new BankRepository(context);
            AbsenceRepository absenceRepository = new AbsenceRepository(context);

            gradesRepository.insert(subjectsAndGrades.gradesList);
            subjectsRepository.insert(subjectsAndGrades.subjectsList);
            bankRepository.insert(bankStatements);
            absenceRepository.insert(absences);

            return Result.success();
        } else {
            return Result.retry();
        }*/
        return Result.success();
    }
}
