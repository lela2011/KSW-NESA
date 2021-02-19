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
    private int type;

    public Absence(String date, String time, String course, int type) {
        this.date = date;
        this.time = time;
        this.course = course;
        this.type = type;
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

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
