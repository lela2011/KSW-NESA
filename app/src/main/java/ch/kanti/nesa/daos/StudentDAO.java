package ch.kanti.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ch.kanti.nesa.tables.Student;

@Dao
public interface StudentDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Student student);

    @Query("SELECT * FROM student_table")
    LiveData<List<Student>> getStudents();

    @Query("SELECT * FROM student_table")
    List<Student> getStudentsSync();

    @Query("DELETE FROM student_table WHERE name = :name")
    void delete(String name);
}
