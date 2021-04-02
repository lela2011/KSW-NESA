package ch.kanti.nesa;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import ch.kanti.nesa.daos.SubjectsDAO;
import ch.kanti.nesa.tables.Subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubjectsRepository {
    final SubjectsDAO subjectsDAO;
    final Context context;

    public SubjectsRepository(Application application) {
        Database database = Database.getInstance(application);
        subjectsDAO = database.subjectsDAO();
        context = application.getApplicationContext();
    }

    public SubjectsRepository(Context context) {
        Database database = Database.getInstance(context);
        subjectsDAO = database.subjectsDAO();
        this.context = context;
    }

    public void insert(List<Subject> subjects) {
        Database.databaseWriteExecutor.execute(() -> {
            List<String> oldSubjectIds = subjectsDAO.getSubjectIds();
            List<String> newSubjectIds = new ArrayList<>();
            for (Subject subject : subjects) {
                newSubjectIds.add(subject.getId());
            }
            Collections.sort(oldSubjectIds);
            Collections.sort(newSubjectIds);
            if(oldSubjectIds.equals(newSubjectIds)) {
                for(Subject subject : subjects) {
                    subjectsDAO.updateAverage(subject.getGradeAverage(), subject.getPluspoints(), subject.getId());
                }
            } else {
                subjectsDAO.deleteAll();
                subjectsDAO.insert(subjects);
            }
        });
    }

    public LiveData<List<Subject>> getSubjects() {
        return subjectsDAO.getSubjects();
    }

    public LiveData<Float> getAverage() {
        return subjectsDAO.getAverage();
    }

    public LiveData<Float> getPluspoints() {
        return subjectsDAO.getPluspoints();
    }

    public void updateName(String id, String name) {
        Database.databaseWriteExecutor.execute(()-> subjectsDAO.updateName(id, name));
    }

    public void deleteAll() {
        subjectsDAO.deleteAll();
    }
}
