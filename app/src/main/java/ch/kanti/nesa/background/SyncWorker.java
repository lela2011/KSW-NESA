package ch.kanti.nesa.background;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ch.kanti.nesa.AbsenceRepository;
import ch.kanti.nesa.App;
import ch.kanti.nesa.BankRepository;
import ch.kanti.nesa.GradesRepository;
import ch.kanti.nesa.LessonRepository;
import ch.kanti.nesa.SubjectsRepository;
import ch.kanti.nesa.objects.LoginAndScrape;
import ch.kanti.nesa.scrapers.Network;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String username = App.sharedPreferences.getString("username","");
        String password = App.sharedPreferences.getString("password","");
        boolean firstLogin = App.sharedPreferences.getBoolean(App.FIRST_LOGIN, true);

        GradesRepository gradesRepository = new GradesRepository(getApplicationContext());
        SubjectsRepository subjectsRepository = new SubjectsRepository(getApplicationContext());
        BankRepository bankRepository = new BankRepository(getApplicationContext());
        AbsenceRepository absenceRepository = new AbsenceRepository(getApplicationContext());
        LessonRepository lessonRepository = new LessonRepository(getApplicationContext());

        if (!firstLogin){
            if (DeviceOnline.check()) {
                LoginAndScrape scrape = Network.checkLoginAndPages(true, false, true, false, username, password);
                if (scrape.isLoginCorrect()) {
                    gradesRepository.insert(scrape.getSubjectsAndGrades().getGradeList());
                    subjectsRepository.insert(scrape.getSubjectsAndGrades().getSubjectList());
                    bankRepository.insert(scrape.getBankStatements());
                    absenceRepository.insert(scrape.getAbsences());
                    lessonRepository.insert(true, scrape.getLessons(), scrape.getExams());

                    return Result.success();
                } else {
                    return Result.failure();
                }
            } else {
                return Result.retry();
            }
        } else {
            return Result.failure();
        }
    }
}
