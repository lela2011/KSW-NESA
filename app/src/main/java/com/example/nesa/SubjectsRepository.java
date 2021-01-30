package com.example.nesa;

import android.app.Application;
import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;

import com.example.nesa.daos.SubjectsDAO;
import com.example.nesa.tables.Subjects;

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
