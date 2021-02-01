package ch.kanti.nesa;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import ch.kanti.nesa.daos.AccountInfoDAO;
import ch.kanti.nesa.daos.BankDAO;
import ch.kanti.nesa.daos.GradesDAO;
import ch.kanti.nesa.daos.SubjectsDAO;
import ch.kanti.nesa.daos.UserDAO;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Grades;
import ch.kanti.nesa.tables.Subjects;
import ch.kanti.nesa.tables.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {User.class, AccountInfo.class, BankStatement.class, Grades.class, Subjects.class}, version = 13, exportSchema = false)
public abstract class Database extends RoomDatabase {
    //initialize Database
    private static volatile Database instance;
    public abstract UserDAO userDAO();
    public abstract AccountInfoDAO accountInfoDAO();
    public abstract BankDAO bankStatementDAO();
    public abstract GradesDAO gradesDAO();
    public abstract SubjectsDAO subjectsDAO();
    public static final int NUMBER_OF_THREADS = 18;
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
