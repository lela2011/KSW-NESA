package ch.kanti.nesa.objects;

import ch.kanti.nesa.tables.Grades;
import ch.kanti.nesa.tables.Subjects;

import java.util.ArrayList;

public class SubjectsAndGrades {
    public ArrayList<Subjects> subjectsList;
    public ArrayList<Grades> gradesList;

    public SubjectsAndGrades(ArrayList<Subjects> subjectsList, ArrayList<Grades> gradesList) {
        this.subjectsList = subjectsList;
        this.gradesList = gradesList;
    }
}
