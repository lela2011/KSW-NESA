package com.example.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bank_table")
public class BankStatement {
    @PrimaryKey(autoGenerate = true)
    private int id;
    public int order;
    public String date;
    public String title;
    public float amount;
    public float saldo;

    public BankStatement(int order, String date, String title, float amount, float saldo) {
        this.order = order;
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.saldo = saldo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
