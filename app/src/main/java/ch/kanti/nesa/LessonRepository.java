package ch.kanti.nesa;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.stream.Collectors;

import ch.kanti.nesa.daos.GradesDAO;
import ch.kanti.nesa.daos.LessonDAO;
import ch.kanti.nesa.daos.SubjectsDAO;
import ch.kanti.nesa.tables.Lesson;

public class LessonRepository {

    final LessonDAO lessonDAO;
    final Context context;

    public LessonRepository(Application application) {
        Database database = Database.getInstance(application);
        lessonDAO = database.lessonDAO();
        context = application.getApplicationContext();
    }

    public LessonRepository(Context context) {
        Database database = Database.getInstance(context);
        lessonDAO = database.lessonDAO();
        this.context = context;
    }

    public void insert(boolean week, List<Lesson> lessons, List<Lesson> exams) {
        Database.databaseWriteExecutor.execute(()->{
            if(lessons.size() != 0) {
                if (week) {
                    lessonDAO.deleteByWeek(lessons.get(0).getWeek());
                    lessonDAO.insert(lessons);
                    List<Lesson> duringDayExams = exams.stream()
                            .filter(c -> c.getLesson() != 0)
                            .collect(Collectors.toList());
                    List<Lesson> fullDayExams = exams.stream()
                            .filter(c -> c.getLesson() == 0)
                            .collect(Collectors.toList());
                    for (Lesson exam : duringDayExams) {
                        lessonDAO.updateExam(exam.getMarking(), exam.getComment(), exam.getColor(), exam.isExam(), exam.getDay(), exam.getLesson(), exam.getSubject());
                    }
                    lessonDAO.insert(fullDayExams);
                } else {
                    lessonDAO.deleteAll();
                    lessonDAO.insert(lessons);
                    List<Lesson> duringDayExams = exams.stream()
                            .filter(c -> c.getLesson() != 0)
                            .collect(Collectors.toList());
                    List<Lesson> fullDayExams = exams.stream()
                            .filter(c -> c.getLesson() == 0)
                            .collect(Collectors.toList());
                    for (Lesson exam : duringDayExams) {
                        lessonDAO.updateExam(exam.getMarking(), exam.getComment(), exam.getColor(), exam.isExam(), exam.getDay(), exam.getLesson(), exam.getSubject());
                    }
                    lessonDAO.insert(fullDayExams);
                }
            }
        });
    }

    public LiveData<List<Lesson>> getLessons(String day) {
        return lessonDAO.getLessons(day);
    }

}
