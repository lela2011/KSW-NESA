package ch.kanti.nesa.objects;

import ch.kanti.nesa.tables.Grade;
import ch.kanti.nesa.tables.Subject;

import java.util.ArrayList;

public class SubjectsAndGrades {
    public final ArrayList<Subject> subjectList;
    public final ArrayList<Grade> gradeList;

    public SubjectsAndGrades(ArrayList<Subject> subjectList, ArrayList<Grade> gradeList) {
        this.subjectList = subjectList;
        this.gradeList = gradeList;
    }
}
