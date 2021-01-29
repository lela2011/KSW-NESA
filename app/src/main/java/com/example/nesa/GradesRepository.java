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
            int newGradesSize = 0;
            if (gradesDAO.size() != 0) {
                List<Grades> oldGrades = gradesDAO.getAllGradesOrdered();
                List<Grades> newGrades = new ArrayList<>(grades);

                for(int i = 0; i < oldGrades.size(); i++){
                    for(int k = 0; k < newGrades.size(); k++) {
                        k = newGradesSize;
                        if(oldGrades.get(i).getId().equals(newGrades.get(k).getId())){
                            newGrades.remove(k);
                            break;
                        }
                        newGradesSize++;
                    }
                }

                List<String> subjectsModified = new ArrayList<>();
                for(int i = 0; i<newGrades.size(); i++){
                    if(!subjectsModified.contains(newGrades.get(i).getSubjectId())){
                        subjectsModified.add(newGrades.get(i).getSubjectId());
                    }
                }

                for(int i = 0; i < subjectsModified.size(); i++){
                    gradesDAO.deleteBySubject(subjectsModified.get(i));
                }

                String subId = null;
                ArrayList<Grades> tempGrades = new ArrayList<>();
                ArrayList<ArrayList> nestedGrades = new ArrayList<>();

                for (int i = 0; i < subjectsModified.size(); i++) {
                    subId = subjectsModified.get(i);
                    for (int k = 0; k < newGrades.size(); k++) {
                        if (subId.equals(newGrades.get(k).getSubjectId())) {

                        }
                    }
                }

            } else {
                gradesDAO.insert(grades);
            }
        });
    }

    LiveData<List<Grades>> getBySubject(String passedSubject) {
        return gradesDAO.getBySubject(passedSubject);
    }
}
