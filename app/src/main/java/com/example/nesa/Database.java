package com.example.nesa;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.nesa.daos.AccountInfoDAO;
import com.example.nesa.daos.BankStatementDAO;
import com.example.nesa.daos.UserDAO;
import com.example.nesa.tables.AccountInfo;
import com.example.nesa.tables.BankStatement;
import com.example.nesa.tables.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {User.class, AccountInfo.class, BankStatement.class}, version = 8, exportSchema = false)
public abstract class Database extends RoomDatabase {
    //initialize Database
    private static volatile Database instance;
    public abstract UserDAO userDAO();
    public abstract AccountInfoDAO accountInfoDAO();
    public abstract BankStatementDAO bankStatementDAO();
    public static final int NUMBER_OF_THREADS = 10;
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
