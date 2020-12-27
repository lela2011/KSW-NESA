package com.example.nesa.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bank_table")
public class BankStatement {
    @PrimaryKey
    @NonNull
    public String pk;
    public int order;
    public String date;
    public String title;
    public float amount;
    public float balance;

    public BankStatement(@NonNull String pk, int order, String date, String title, float amount, float balance) {
        this.pk = pk;
        this.order = order;
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.balance = balance;
    }
}
