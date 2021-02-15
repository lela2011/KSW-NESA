package ch.kanti.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "absences_table")
public class Absence {
    @PrimaryKey(autoGenerate = true)
    private int pk;
    private String date;
    private String time;
    private String course;

    public Absence(String date, String time, String course) {
        this.date = date;
        this.time = time;
        this.course = course;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
