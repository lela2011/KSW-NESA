package ch.kanti.nesa.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grades_table")
public class Grades {
    @NonNull
    @PrimaryKey
    private String id;
    private String subjectId;
    private String exam;
    private float grade;
    private float weight;
    private String date;
    private int subjectNumber;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    private int order;

    public Grades(String id, String exam, float grade, float weight, String date, String subjectId, int order, int subjectNumber) {
        this.id = id;
        this.exam = exam;
        this.grade = grade;
        this.weight = weight;
        this.date = date;
        this.subjectId = subjectId;
        this.order = order;
        this.subjectNumber = subjectNumber;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public int getSubjectNumber() {
        return subjectNumber;
    }

    public void setSubjectNumber() {
        this.subjectNumber = subjectNumber;
    }
}
