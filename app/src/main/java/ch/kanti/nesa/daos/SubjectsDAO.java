package ch.kanti.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import ch.kanti.nesa.tables.Subjects;

import java.util.List;

@Dao
public interface SubjectsDAO {
    @Update
    void update(Subjects subjects);

    @Insert
    void insert(List<Subjects> subjects);

    @Query("DELETE FROM subjects_table")
    void deleteAll();

    @Query("SELECT * FROM subjects_table")
    LiveData<List<Subjects>> getSubjects();
}
