package com.example.nesa.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nesa.tables.Grades;

@Dao
public interface GradesDAO {
    @Update
    void update(Grades grades);

    @Insert
    void insert(Grades grades);

    @Delete
    void delete(Grades grades);

    @Query("SELECT * FROM grades_table WHERE subject=subject")
    void getBySubject(String subject);
}
