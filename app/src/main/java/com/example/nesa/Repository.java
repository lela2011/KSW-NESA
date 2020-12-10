package com.example.nesa;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.nesa.tables.User;

public class Repository {
    private UserDAO userDao;
    //initialize repository
    public Repository(Application application) {
        Database database = Database.getInstance(application);
        userDao = database.userDAO();
    }
    //update database
    public void update(User user) {
        Database.databaseWriteExecutor.execute(() -> {
            userDao.update(user);
        });
    }
    //insert into database
    public void insert(User user) {
        Database.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }
    //delete from database
    public void delete(User user) {
        Database.databaseWriteExecutor.execute(() -> {
            userDao.delete(user);
        });
    }
    //get credentials
    public LiveData<User> getCredentials() {
        return userDao.getCredentials();
    }
    //get table size
    public LiveData<Integer> getTableSize() {
        return userDao.getTableSize();
    }
}
