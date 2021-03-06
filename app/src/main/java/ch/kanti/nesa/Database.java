package ch.kanti.nesa;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import ch.kanti.nesa.daos.AbsenceDAO;
import ch.kanti.nesa.daos.AccountInfoDAO;
import ch.kanti.nesa.daos.BankDAO;
import ch.kanti.nesa.daos.GradesDAO;
import ch.kanti.nesa.daos.LessonDAO;
import ch.kanti.nesa.daos.StudentDAO;
import ch.kanti.nesa.daos.SubjectsDAO;
import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Grade;
import ch.kanti.nesa.tables.Lesson;
import ch.kanti.nesa.tables.Student;
import ch.kanti.nesa.tables.Subject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {AccountInfo.class, BankStatement.class, Grade.class, Subject.class, Absence.class, Student.class, Lesson.class}, version = 27, exportSchema = false)
public abstract class Database extends RoomDatabase {
    //initialize Database
    private static volatile Database instance;
    public abstract AccountInfoDAO accountInfoDAO();
    public abstract BankDAO bankStatementDAO();
    public abstract GradesDAO gradesDAO();
    public abstract SubjectsDAO subjectsDAO();
    public abstract AbsenceDAO absenceDAO();
    public abstract StudentDAO studentDAO();
    public abstract LessonDAO lessonDAO();
    public static final int NUMBER_OF_THREADS = 20;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static Database getInstance(Context context) {
        if (instance==null) {
            synchronized (Database.class){
                if (instance==null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class,
                            "database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
