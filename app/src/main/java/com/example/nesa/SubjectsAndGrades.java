package com.example.nesa;

import com.example.nesa.tables.Grades;
import com.example.nesa.tables.Subjects;

import java.util.ArrayList;

public class SubjectsAndGrades {
    public ArrayList<Subjects> subjectsList;
    public ArrayList<Grades> gradesList;

    public SubjectsAndGrades(ArrayList<Subjects> subjectsList, ArrayList<Grades> gradesList) {
        this.subjectsList = subjectsList;
        this.gradesList = gradesList;
    }
}
