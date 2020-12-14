package com.example.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nesa.tables.AccountInfo;

import java.util.List;

@Dao
public interface AccountInfoDAO {
    //update account_table
    @Update
    void update(AccountInfo info);

    //insert into account_table
    @Insert
    void insert(AccountInfo info);

    //delete from account_table
    @Delete
    void delete(AccountInfo info);

    //select all items from account_table and order by ascending id
    @Query("SELECT * FROM account_table ORDER BY `order` ASC")
    LiveData<List<AccountInfo>> getAccountInfo();

    @Query("SELECT COUNT(*) FROM account_table")
    LiveData<Integer> getTableSize();
}
