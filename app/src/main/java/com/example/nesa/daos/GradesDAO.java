package com.example.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nesa.tables.Grades;

import java.util.List;

@Dao
public interface GradesDAO {
    @Update
    void update(Grades grades);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Grades> grades);

    @Delete
    void delete(Grades grades);

    @Query("SELECT * FROM grades_table WHERE subjectId = :passedSubject")
    LiveData<List<Grades>> getBySubject(String passedSubject);

    @Query("SELECT * FROM grades_table ORDER BY subjectNumber ASC, `order` ASC")
    List<Grades> getAllGradesOrdered();

    @Query("SELECT COUNT(*) FROM grades_table")
    int size();
}
