package ch.kanti.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grades_table")
public class Grade {
    @PrimaryKey(autoGenerate = true)
    int id;
    private final String subjectId;
    private final String exam;
    private final float grade;
    private final float weight;
    private final String date;
    private final int subjectNumber;

    public int getOrder() {
        return order;
    }

    private final int order;

    public Grade(String exam, String subjectId, String date, float grade, float weight, int order, int subjectNumber) {
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

    public String getDate() {
        return date;
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

    public float getGrade() {
        return grade;
    }

    public float getWeight() {
        return weight;
    }

    public int getSubjectNumber() {
        return subjectNumber;
    }

    public boolean compare(Grade toCompare) {
        return this.getExam().equals(toCompare.getExam()) &&
                this.getSubjectId().equals(toCompare.getSubjectId()) &&
                this.getDate().equals(toCompare.getDate()) &&
                this.getGrade() == toCompare.getGrade() &&
                this.getWeight() == toCompare.getWeight();
    }

    public boolean gradeModified(Grade toCompare) {
        return this.getExam().equals(toCompare.getExam()) &&
                this.getSubjectId().equals(toCompare.getSubjectId()) &&
                this.getDate().equals(toCompare.getDate());
    }
}
