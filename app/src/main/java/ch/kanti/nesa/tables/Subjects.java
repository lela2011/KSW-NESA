package ch.kanti.nesa.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects_table")
public class Subjects {
    @NonNull
    @PrimaryKey
    private String id;
    private String weight;
    private String subjectName;
    private Float pluspoints;
    private Float gradeAverage;
    private int order;
    private int countsPluspoints;
    private int countsAverage;
    private int isDisplayed;
    private int partOfSet;
    private int isSet;

    public Subjects(String subjectName,String weight, Float gradeAverage, Float pluspoints, String id, int order, int countsPluspoints, int countsAverage, int isDisplayed, int partOfSet, int isSet) {
        this.subjectName = subjectName;
        this.weight = weight;
        this.gradeAverage = gradeAverage;
        this.pluspoints = pluspoints;
        this.id = id;
        this.order = order;
        this.countsPluspoints = countsPluspoints;
        this.countsAverage = countsAverage;
        this.isDisplayed = isDisplayed;
        this.partOfSet = partOfSet;
        this.isSet = isSet;
    }

    public String getId() {
        return id;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
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

    public int getCountsPluspoints() {
        return countsPluspoints;
    }

    public void setCountsPluspoints(int countsPluspoints) {
        this.countsPluspoints = countsPluspoints;
    }

    public int getCountsAverage() {
        return countsAverage;
    }

    public void setCountsAverage(int countsAverage) {
        this.countsAverage = countsAverage;
    }

    public int getIsDisplayed() {
        return isDisplayed;
    }

    public void setIsDisplayed(int isDisplayed) {
        this.isDisplayed = isDisplayed;
    }

    public int getPartOfSet() {
        return partOfSet;
    }

    public void setPartOfSet(int partOfSet) {
        this.partOfSet = partOfSet;
    }

    public int getIsSet() {
        return isSet;
    }

    public void setIsSet(int isSet) {
        this.isSet = isSet;
    }
}