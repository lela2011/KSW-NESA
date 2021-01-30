package ch.kanti.nesa.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects_table")
public class Subjects {
    @NonNull
    @PrimaryKey
    private String id;
    private String subjectName;
    private Float pluspoints;
    private Float gradeAverage;
    private int order;
    private int isSet;

    public Subjects(String subjectName, Float gradeAverage, String id, int order, int isSet) {
        this.subjectName = subjectName;
        this.gradeAverage = gradeAverage;
        this.id = id;
        this.order = order;
        this.isSet = isSet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Float getPluspoints() {
        return pluspoints;
    }

    public void setPluspoints(Float pluspoints) {
        this.pluspoints = pluspoints;
    }

    public Float getGradeAverage() {
        return gradeAverage;
    }

    public void setGradeAverage(Float gradeAverage) {
        this.gradeAverage = gradeAverage;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}