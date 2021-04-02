package ch.kanti.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import ch.kanti.nesa.tables.Grade;

import java.util.List;

@Dao
public interface GradesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Grade> grades);

    @Query("DELETE FROM grades_table")
    void deleteAll();

    @Query("SELECT * FROM grades_table WHERE subjectId = :passedSubject")
    LiveData<List<Grade>> getBySubject(String passedSubject);

    @Query("SELECT * FROM grades_table ORDER BY subjectNumber ASC, `order` ASC")
    List<Grade> getAllGradesOrdered();

    @Query("SELECT COUNT(*) FROM grades_table")
    int size();

    @Query("DELETE FROM grades_table WHERE subjectId = :id AND exam = :name AND date = :date")
    void deleteByGrade(String id, String name, String date);

    @Query("UPDATE grades_table SET grade = :grade, weight = :weight WHERE subjectId = :id AND exam = :name AND date = :date")
    void updateGrade(String id, String name, String date, float grade, float weight);
}
