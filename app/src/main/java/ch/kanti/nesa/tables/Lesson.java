package ch.kanti.nesa.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lesson_table")
public class Lesson {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final String day;
    private final String week;
    private final String startTime;
    private final String endTime;
    private final String subject;
    private final String teacherShort;
    private final String room;
    private final String marking;
    private final String comment;
    private final boolean isExam;
    private final int lesson;
    private final int sublesson;
    private int siblingLessons;
    private String color;

    public Lesson(String day, String week, String startTime, String endTime, String subject, String teacherShort, String room, String marking, String comment, String color, boolean isExam, int lesson, int sublesson) {
        this.week = week;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.teacherShort = teacherShort;
        this.room = room;
        this.marking = marking;
        this.comment = comment;
        this.color = color;
        this.isExam = isExam;
        this.lesson = lesson;
        this.sublesson = sublesson;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSubject() {
        return subject;
    }

    public String getRoom() {
        return room;
    }

    public String getMarking() {
        return marking;
    }

    public String getComment() {
        return comment;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isExam() {
        return isExam;
    }

    public String getDay() {
        return day;
    }

    public String getWeek() {
        return week;
    }

    public int getLesson() {
        return lesson;
    }

    public int getSublesson() {
        return sublesson;
    }

    public int getSiblingLessons() {
        return siblingLessons;
    }

    public void setSiblingLessons(int siblingLessons) {
        this.siblingLessons = siblingLessons;
    }

    public String getTeacherShort() {
        return teacherShort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
