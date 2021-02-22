package ch.kanti.nesa;

import android.app.Application;

import androidx.lifecycle.LiveData;

import ch.kanti.nesa.daos.SubjectsDAO;
import ch.kanti.nesa.tables.Subjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SubjectsRepository {
    SubjectsDAO subjectsDAO;

    public SubjectsRepository(Application application) {
        Database database = Database.getInstance(application);
        subjectsDAO = database.subjectsDAO();
    }

    public void insert(List<Subjects> subjects) {
        Database.databaseWriteExecutor.execute(() -> {
            List<String> oldSubjectIds = subjectsDAO.getSubjectIds();
            List<String> newSubjectIds = new ArrayList<>();
            for (Subjects subject : subjects) {
                newSubjectIds.add(subject.getId());
            }
            Collections.sort(oldSubjectIds);
            Collections.sort(newSubjectIds);
            if(oldSubjectIds.equals(newSubjectIds)) {
                for(Subjects subject : subjects) {
                    subjectsDAO.updateAverage(subject.getGradeAverage(), subject.getPluspoints(), subject.getId());
                }
            } else {
                subjectsDAO.deleteAll();
                subjectsDAO.insert(subjects);
            }
        });
    }

    public LiveData<List<Subjects>> getSubjects() {
        return subjectsDAO.getSubjects();
    }

    public LiveData<Float> getAverage() {
        return subjectsDAO.getAverage();
    }

    public LiveData<Float> getPluspoints() {
        return subjectsDAO.getPluspoints();
    }

    public void updateNameCounts(String name, int countsPluspoints, int countsAverage, String id) {
        Database.databaseWriteExecutor.execute(()->{
            subjectsDAO.updateNamesCount(name, countsPluspoints, countsAverage, id);
        });
    }

    public LiveData<List<String>> getNonSetSubjectIds() {
        return subjectsDAO.getNonSetSubjectIds();
    }

    public void insertSubjectSet(Subjects subject) {
        Database.databaseWriteExecutor.execute(() -> {
            ArrayList<String> subjectIds = new ArrayList<String>();
            subjectIds.addAll(Arrays.asList(subject.getId().split("&")));
            for (String id : subjectIds) {
                subjectsDAO.updateToSet(id, 0, 1);
            }
            subjectsDAO.insertSingle(subject);
        });
    }

    /*public LiveData<List<Subjects>> getSetLiveDAte(List<String> ids) {
        for (String id : ids) {
            subjectsDAO.getSubjectById(id);
        }
    }*/
}
