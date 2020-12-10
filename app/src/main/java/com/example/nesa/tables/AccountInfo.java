package com.example.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "account_table")
public class AccountInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public String keyName;
    public String keyValue;

    public AccountInfo(String keyName, String keyValue) {
        this.keyName = keyName;
        this.keyValue = keyValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
