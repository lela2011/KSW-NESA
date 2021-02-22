package ch.kanti.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("UPDATE subjects_table SET isDisplayed = :isDisplayed, partOfSet = :partOfSet WHERE id = :id")
    void updateToSet(String id, int isDisplayed, int partOfSet);

    @Query("DELETE FROM subjects_table")
    void deleteAll();

    @Query("SELECT * FROM subjects_table WHERE isDisplayed = 1")
    LiveData<List<Subjects>> getSubjects();

    @Query("SELECT * FROM subjects_table WHERE id = :id")
    LiveData<Subjects> getSubjectById(String id);

    @Query("SELECT id FROM subjects_table WHERE isSet = 0")
    List<String> getSubjectIds();

    @Query("SELECT avg(gradeAverage) FROM subjects_table WHERE countsAverage = 1 AND gradeAverage != -1.0")
    LiveData<Float> getAverage();

    @Query("SELECT gradeAverage FROM subjects_table WHERE id = :id")
    Float getSubjectAverage(String id);

    @Query("SELECT sum(pluspoints) FROM subjects_table WHERE countsPluspoints = 1 AND pluspoints != -10")
    LiveData<Float> getPluspoints();

    @Query("UPDATE subjects_table SET subjectName = :name, countsPluspoints = :countsPluspoints, countsAverage = :countsAverage WHERE id = :id")
    void updateNamesCount(String name, int countsPluspoints, int countsAverage, String id);

    @Query("SELECT id FROM subjects_table WHERE partOfSet = 0 AND isSet = 0")
    LiveData<List<String>> getNonSetSubjectIds();
}
