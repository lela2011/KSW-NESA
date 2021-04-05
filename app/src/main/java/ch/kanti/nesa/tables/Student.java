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
    private final boolean bilingual;
    private final String course;
    private final String address;
    private final String phone;

    public Student(String name, String gender, String degree, boolean bilingual, String course, String address, String phone) {
        this.name = name;
        this.gender = gender;
        this.degree = degree;
        this.bilingual = bilingual;
        this.course = course;
        this.address = address;
        this.phone = phone;
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

    public boolean isBilingual() {
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

    public boolean equals(Student student) {
        return this.name.equals(student.getName()) &&
                this.gender.equals(student.getGender()) &&
                this.degree.equals(student.getDegree()) &&
                this.bilingual == student.isBilingual() &&
                this.course.equals(student.getCourse()) &&
                this.address.equals(student.getAddress()) &&
                this.phone.equals(student.getPhone());
    }
}
