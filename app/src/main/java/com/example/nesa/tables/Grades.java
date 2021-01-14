package com.example.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grades_table")
public class Grades {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String subjectId;
    private String exam;
    private float grade;
    private float weight;
    private String date;

    public Grades(String exam, float grade, float weight, String date, String subjectId) {
        this.exam = exam;
        this.grade = grade;
        this.weight = weight;
        this.date = date;
        this.subjectId = subjectId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String subject) {
        this.exam = subject;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
