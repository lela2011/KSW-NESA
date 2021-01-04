package com.example.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grades_table")
public class Grades {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String subject;
    private float grade;

    public Grades(String subject, float grade) {
        this.subject = subject;
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }
}
