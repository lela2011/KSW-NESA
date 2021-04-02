package ch.kanti.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "absences_table")
public class Absence {
    @PrimaryKey(autoGenerate = true)
    private int pk;
    private final String date;
    private final String time;
    private final String course;
    private final int type;
    private final int excused;

    public Absence(String date, String time, String course, int type, int excused) {
        this.date = date;
        this.time = time;
        this.course = course;
        this.type = type;
        this.excused = excused;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCourse() {
        return course;
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

    public int getExcused() {
        return excused;
    }

    public boolean compare(Absence toCompare) {
        return this.getDate().equals(toCompare.getDate()) &&
                this.getType() == toCompare.getType() &&
                this.getCourse().equals(toCompare.getCourse()) &&
                this.getTime().equals(toCompare.getTime()) &&
                this.getExcused() == toCompare.getExcused();
    }

    public boolean modified(Absence toCompare) {
        return this.getDate().equals(toCompare.getDate()) &&
                this.getType() == toCompare.getType() &&
                this.getCourse().equals(toCompare.getCourse()) &&
                this.getTime().equals(toCompare.getTime());
    }
}
