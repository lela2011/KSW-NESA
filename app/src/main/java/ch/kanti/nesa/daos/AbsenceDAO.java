package ch.kanti.nesa.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ch.kanti.nesa.tables.Absence;

@Dao
public interface AbsenceDAO {
    @Insert
    void insertAll(List<Absence> absences);

    @Query("DELETE FROM absences_table")
    void deleteAll();
}
