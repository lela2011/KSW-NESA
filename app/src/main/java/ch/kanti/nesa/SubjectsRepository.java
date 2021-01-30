package ch.kanti.nesa;

import android.app.Application;

import androidx.lifecycle.LiveData;

import ch.kanti.nesa.daos.SubjectsDAO;
import ch.kanti.nesa.tables.Subjects;

import java.util.List;

public class SubjectsRepository {
    SubjectsDAO subjectsDAO;

    public SubjectsRepository(Application application) {
        Database database = Database.getInstance(application);
        subjectsDAO = database.subjectsDAO();
    }

    public void insert(List<Subjects> subjects) {
        Database.databaseWriteExecutor.execute(() -> {
            subjectsDAO.deleteAll();
            subjectsDAO.insert(subjects);
        });
    }

    public LiveData<List<Subjects>> getSubjects() {
        return subjectsDAO.getSubjects();
    }
}
