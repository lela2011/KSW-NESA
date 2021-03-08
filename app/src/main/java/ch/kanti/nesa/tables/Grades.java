package ch.kanti.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grades_table")
public class Grades {
    @PrimaryKey(autoGenerate = true)
    int id;
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

    public Grades(String exam, String subjectId, String date, float grade, float weight, int order, int subjectNumber) {
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

    public int getSubjectNumber() {
        return subjectNumber;
    }

    public void setSubjectNumber(int subjectNumber) {
        this.subjectNumber = subjectNumber;
    }

    public boolean compare(Grades toCompare) {
        return this.getExam().equals(toCompare.getExam()) &&
                this.getSubjectId().equals(toCompare.getSubjectId()) &&
                this.getDate().equals(toCompare.getDate()) &&
                this.getGrade() == toCompare.getGrade() &&
                this.getWeight() == toCompare.getWeight();
    }

    public boolean gradeModified(Grades toCompare) {
        return this.getExam().equals(toCompare.getExam()) &&
                this.getSubjectId().equals(toCompare.getSubjectId()) &&
                this.getDate().equals(toCompare.getDate());
    }
}
