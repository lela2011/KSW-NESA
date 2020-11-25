package com.example.nesa;

import android.app.Application;

public class Repository {
    private UserDAO userDao;

    public Repository(Application application) {
        Database database = Database.getInstance(application);
        userDao = database.userDAO();
    }
}
