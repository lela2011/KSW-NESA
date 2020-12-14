package com.example.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "account_table")
public class AccountInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    public String value;
    public int order;

    public AccountInfo(String value, int order) {
        this.value = value;
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
