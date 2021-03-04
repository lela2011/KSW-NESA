package ch.kanti.nesa;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

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
        Document gradesPage, absencesPage, bankPage;
        gradesPage = DocumentScraper.getMarkPage();
        absencesPage = DocumentScraper.getAbsencesPage();
        bankPage = DocumentScraper.getBankPage();

        ArrayList<Absence> absences = ContentScrapers.scrapeAbsences(absencesPage);
        SubjectsAndGrades subjectsAndGrades = ContentScrapers.scrapeMarks(gradesPage);
        ArrayList<BankStatement> bankStatements = ContentScrapers.scrapeBank(bankPage);


        return null;
    }
}
