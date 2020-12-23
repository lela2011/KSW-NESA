package com.example.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nesa.tables.BankStatement;

import java.util.List;

@Dao
public interface BankStatementDAO {
    @Update
    void update(List<BankStatement> statement);

    //to insert all of the scraped bank table at the beginning
    @Insert
    void insertAll(List<BankStatement> statement);

    //to insert a single new line of the bank statement table from the scraped site
    @Insert
    void insert(BankStatement statement);

    //delete a single row
    @Delete
    void delete(BankStatement statement);

    @Query("DELETE FROM bank_table")
    void deleteAll();

    //get all of the rows in ascending order (oldest to newest)
    @Query("SELECT * FROM bank_table ORDER BY `order` ASC")
    LiveData<List<BankStatement>> getBankStatement();

    @Query("SELECT saldo FROM bank_table ORDER BY `order` DESC LIMIT 1")
    LiveData<Float> getBalance();
}
