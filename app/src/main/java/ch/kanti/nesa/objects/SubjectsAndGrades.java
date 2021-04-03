package ch.kanti.nesa.objects;

import ch.kanti.nesa.tables.Grade;
import ch.kanti.nesa.tables.Subject;

import java.util.ArrayList;

public class SubjectsAndGrades {
    private final ArrayList<Subject> subjectList;
    private final ArrayList<Grade> gradeList;

    public SubjectsAndGrades(ArrayList<Subject> subjectList, ArrayList<Grade> gradeList) {
        this.subjectList = subjectList;
        this.gradeList = gradeList;
    }

    public ArrayList<Subject> getSubjectList() {
        return subjectList;
    }

    public ArrayList<Grade> getGradeList() {
        return gradeList;
    }
}
