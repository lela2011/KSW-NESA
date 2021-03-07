package ch.kanti.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
