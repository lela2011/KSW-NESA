package com.example.nesa;

import android.app.Application;

import com.example.nesa.daos.GradesDAO;
import com.example.nesa.tables.Grades;

import java.util.List;

public class GradesRepository {
    GradesDAO gradesDAO;

    public GradesRepository(Application application) {
        Database database = Database.getInstance(application);
        gradesDAO = database.gradesDAO();
    }

    public void insert(List<Grades> grades) {
        Database.databaseWriteExecutor.execute(()->{
            gradesDAO.insert(grades);
        });
    }


}
