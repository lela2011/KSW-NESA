package com.example.nesa;

import android.app.Application;
import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;

import com.example.nesa.daos.AccountInfoDAO;
import com.example.nesa.daos.UserDAO;
import com.example.nesa.tables.AccountInfo;
import com.example.nesa.tables.User;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoginRepository {
    private UserDAO userDao;
    //initialize repository
    public LoginRepository(Application application) {
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
