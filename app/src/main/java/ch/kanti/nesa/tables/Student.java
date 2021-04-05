package ch.kanti.nesa.tables;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "student_table")
public class Student {

    @PrimaryKey(autoGenerate = true)
    private int pk;
    private final String name;
    private final String gender;
    private final String degree;
    private final String bilingual;
    private final String course;
    private final String address;
    private final String phone;
    private final String additionalCourses;
    private final String status;

    public Student(String name, String gender, String degree, String bilingual, String course, String address, String phone, String additionalCourses, String status) {
        this.name = name;
        this.gender = gender;
        this.degree = degree;
        this.bilingual = bilingual;
        this.course = course;
        this.address = address;
        this.phone = phone;
        this.additionalCourses = additionalCourses;
        this.status = status;
    }

    public int getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getDegree() {
        return degree;
    }

    public String getBilingual() {
        return bilingual;
    }

    public String getCourse() {
        return course;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getAdditionalCourses() {
        return additionalCourses;
    }

    public String getStatus() {
        return status;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public boolean equals(Student student) {
        return this.name.equals(student.getName()) &&
                this.gender.equals(student.getGender()) &&
                this.degree.equals(student.getDegree()) &&
                this.bilingual == student.getBilingual() &&
                this.course.equals(student.getCourse()) &&
                this.address.equals(student.getAddress()) &&
                this.phone.equals(student.getPhone()) &&
                this.additionalCourses.equals(student.getAdditionalCourses()) &&
                this.status.equals(student.getStatus());
    }
}
