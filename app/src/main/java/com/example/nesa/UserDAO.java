package com.example.nesa;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDAO {

    @Update
    void update(User user);

    @Insert
    void insert(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM login_table")
    LiveData<List<User>> getUser();
}
