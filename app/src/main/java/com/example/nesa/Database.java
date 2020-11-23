package com.example.nesa;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
}
