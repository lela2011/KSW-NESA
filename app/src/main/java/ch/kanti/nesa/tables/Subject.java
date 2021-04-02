package ch.kanti.nesa.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects_table")
public class Subject {
    @NonNull
    @PrimaryKey
    private final String id;
    private final String weight;
    private final String subjectName;
    private final Float pluspoints;
    private final Float gradeAverage;
    private final int order;
    private final int countsPluspoints;
    private final int countsAverage;

    

    public Subject(String subjectName, String weight, Float gradeAverage, Float pluspoints, @NonNull String id, int order, int countsPluspoints, int countsAverage) {
        this.subjectName = subjectName;
        this.weight = weight;
        this.gradeAverage = gradeAverage;
        this.pluspoints = pluspoints;
        this.id = id;
        this.order = order;
        this.countsPluspoints = countsPluspoints;
        this.countsAverage = countsAverage;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getWeight() {
        return weight;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Float getPluspoints() {
        return pluspoints;
    }

    public Float getGradeAverage() {
        return gradeAverage;
    }

    public int getOrder() {
        return order;
    }

    public int getCountsPluspoints() {
        return countsPluspoints;
    }

    public int getCountsAverage() {
        return countsAverage;
    }

}