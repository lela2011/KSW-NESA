package ch.kanti.nesa.tables;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Entity(tableName = "login_table")
public class User {

    //column names
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password;
    private String department;
    //initialize object
    public User(String username, String password, String department) {
        this.username = username;
        this.password = password;
        this.department = department;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
