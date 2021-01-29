package com.example.nesa;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.nesa.daos.GradesDAO;
import com.example.nesa.tables.Grades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradesRepository {
    GradesDAO gradesDAO;

    public GradesRepository(Application application) {
        Database database = Database.getInstance(application);
        gradesDAO = database.gradesDAO();
    }

    public void insert(List<Grades> grades) {
        Database.databaseWriteExecutor.execute(()->{
            int newGradesSize = 0;
            int oldGradesSize = gradesDAO.size();
            if (oldGradesSize < grades.size() && oldGradesSize != 0) {
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

                List<Grades> gradesToAdd = new ArrayList<>();

                for(Grades grade : grades) {
                    if(subjectsModified.contains(grade.getSubjectId())){
                        gradesToAdd.add(grade);
                    }
                }

                gradesDAO.insert(gradesToAdd);

            } else if (grades.size() < oldGradesSize) {
                gradesDAO.deleteAll();
                gradesDAO.insert(grades);
            } else {
                gradesDAO.insert(grades);
            }
        });
    }

    LiveData<List<Grades>> getBySubject(String passedSubject) {
        return gradesDAO.getBySubject(passedSubject);
    }
}
