package ch.kanti.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import ch.kanti.nesa.tables.Subjects;

import java.util.List;

@Dao
public interface SubjectsDAO {
    //@Update
    //void update(Subjects subjects);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Subjects> subjects);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertSingle(Subjects subjects);

    @Query("UPDATE subjects_table SET gradeAverage = :average, pluspoints = :pluspoints WHERE id = :id")
    void updateAverage(Float average, Float pluspoints, String id);

    @Query("DELETE FROM subjects_table")
    void deleteAll();

    @Query("SELECT * FROM subjects_table")
    LiveData<List<Subjects>> getSubjects();

    @Query("SELECT id FROM subjects_table")
    List<String> getSubjectIds();

    @Query("SELECT avg(gradeAverage) FROM subjects_table WHERE countsAverage = 1 AND gradeAverage != -1.0")
    LiveData<Float> getAverage();

    @Query("SELECT gradeAverage FROM subjects_table WHERE id = :id")
    Float getSubjectAverage(String id);

    @Query("SELECT pluspoints FROM subjects_table WHERE id = :id")
    Float getSubjectPluspoints(String id);

    @Query("SELECT sum(pluspoints) FROM subjects_table WHERE countsPluspoints = 1 AND pluspoints != -10")
    LiveData<Float> getPluspoints();

    @Query("UPDATE subjects_table SET subjectName = :name, countsPluspoints = :countsPluspoints, countsAverage = :countsAverage WHERE id = :id")
    void updateNamesCount(String name, int countsPluspoints, int countsAverage, String id);
}
