package ch.kanti.nesa.daos;

import androidx.lifecycle.LiveData;
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

    @Query("SELECT * FROM absences_table WHERE excused = 0")
    LiveData<List<Absence>> getAbsences();

    @Query("SELECT * FROM absences_table")
    List<Absence> getAbsencesSync();

    @Query("DELETE FROM absences_table WHERE date = :date AND time = :time AND course = :course AND type = :type")
    void deleteByAbsence(String date, String time, String course, int type);

    @Query("UPDATE absences_table SET excused = :excused WHERE date = :date AND time = :time AND course = :course AND type = :type")
    void updateAbsence(String date, String time, String course, int type, int excused);

    @Query("SELECT COUNT(*) FROM absences_table WHERE excused=0")
    LiveData<Integer> getAbsenceSize();
}
