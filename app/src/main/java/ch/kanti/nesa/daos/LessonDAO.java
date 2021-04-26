package ch.kanti.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ch.kanti.nesa.tables.Lesson;

@Dao
public interface LessonDAO {

    @Insert
    void insert(List<Lesson> lessons);

    @Query("DELETE FROM lesson_table")
    void deleteAll();

    @Query("DELETE FROM lesson_table WHERE week = :week")
    void deleteByWeek(String week);

    @Query("SELECT * FROM lesson_table WHERE day = :day ORDER BY lesson ASC, sublesson ASC")
    LiveData<List<Lesson>> getLessons(String day);

    @Query("UPDATE lesson_table SET marking = :marking, comment = :comment, color = :color, isExam = :isExam WHERE day = :day AND lesson = :lesson AND subject = :subject")
    void updateExam(String marking, String comment, String color, boolean isExam, String day, int lesson, String subject);

    @Query("SELECT * FROM lesson_table WHERE day = :day AND lesson = :lesson")
    LiveData<List<Lesson>> getNextLesson(String day, String lesson);
}
