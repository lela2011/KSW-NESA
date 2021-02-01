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

    @Query("UPDATE subjects_table SET gradeAverage = :average WHERE id = :id")
    void updateAverage(Float average, String id);

    @Query("DELETE FROM subjects_table")
    void deleteAll();

    @Query("SELECT * FROM subjects_table")
    LiveData<List<Subjects>> getSubjects();

    @Query("SELECT id FROM subjects_table WHERE isSet = 0")
    List<String> getSubjectIds();
}
