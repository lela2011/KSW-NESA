package com.example.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nesa.tables.AccountInfo;

import java.util.List;

@Dao
public interface AccountInfoDAO {
    //insert into account_table
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AccountInfo> infos);

    //select all items from account_table and order by ascending id
    @Query("SELECT * FROM account_table ORDER BY `order` ASC")
    LiveData<List<AccountInfo>> getAccountInfoLive();

    @Query("SELECT * FROM account_table ORDER BY `order` ASC")
    List<AccountInfo> getAccountInfo();
}
