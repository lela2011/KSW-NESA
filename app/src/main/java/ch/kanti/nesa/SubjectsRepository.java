package ch.kanti.nesa;

import android.app.Application;

import androidx.lifecycle.LiveData;

import ch.kanti.nesa.daos.SubjectsDAO;
import ch.kanti.nesa.tables.Subjects;

import java.util.ArrayList;
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
                    subjectsDAO.updateAverage(subject.getGradeAverage(), subject.getId());
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
}
