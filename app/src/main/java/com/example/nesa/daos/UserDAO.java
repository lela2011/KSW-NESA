package com.example.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nesa.tables.User;

@Dao
public interface UserDAO {
    //update entry
    @Update
    void update(User user);
    //insert entry
    @Insert
    void insert(User user);
    //delete entry
    @Delete
    void delete(User user);
    //get credentials
    @Query("SELECT * FROM login_table")
    LiveData<User> getCredentials();
    //get table size
    @Query("SELECT COUNT(*) FROM login_table")
    LiveData<Integer> getTableSize();
}
