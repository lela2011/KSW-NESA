package com.example.nesa;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "login_table")
public class User {

    //column names
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password;
    //initialize object
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    //get id
    public int getId() {
        return id;
    }
    //set id
    public void setId(int id) {
        this.id = id;
    }
    //get username
    public String getUsername() {
        return username;
    }
    //set username
    public void setUsername(String username) {
        this.username = username;
    }
    //get password
    public String getPassword() {
        return password;
    }
    //set password
    public void setPassword(String password) {
        this.password = password;
    }
}
