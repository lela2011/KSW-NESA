package com.example.nesa;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.nesa.daos.GradesDAO;
import com.example.nesa.tables.Grades;

import java.util.ArrayList;
import java.util.List;

public class GradesRepository {
    GradesDAO gradesDAO;

    public GradesRepository(Application application) {
        Database database = Database.getInstance(application);
        gradesDAO = database.gradesDAO();
    }

    public void insert(List<Grades> grades) {
        Database.databaseWriteExecutor.execute(()->{
            if (gradesDAO.size() != 0) {
                List<Grades> orderedGrades = gradesDAO.getAllGradesOrdered();
                List<Grades> newGrades = new ArrayList<>(grades);

                for (int i = 0; i < grades.size(); i++) {
                    for (int k = 0; k < orderedGrades.size(); k++) {
                        if (grades.get(i).equals(grades.get(k))) {
                            newGrades.remove(i);
                            orderedGrades.remove(k);
                            break;
                        }
                    }
                }
                gradesDAO.insert(newGrades);
            } else {
                gradesDAO.insert(grades);
            }
        });
    }

    LiveData<List<Grades>> getBySubject(String passedSubject) {
        return gradesDAO.getBySubject(passedSubject);
    }
}
