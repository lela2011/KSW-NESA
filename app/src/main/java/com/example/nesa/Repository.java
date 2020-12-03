package com.example.nesa;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Repository {
    private UserDAO userDao;

    public Repository(Application application) {
        Database database = Database.getInstance(application);
        userDao = database.userDAO();
    }

    public void update(User user) {
        Database.databaseWriteExecutor.execute(() -> {
            userDao.update(user);
        });
    }

    public void insert(User user) {
        Database.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    public void delete(User user) {
        Database.databaseWriteExecutor.execute(() -> {
            userDao.delete(user);
        });
    }

    public LiveData<List<User>> getCredentials(int id) {
        return userDao.getCredentials(id);
    }
}
