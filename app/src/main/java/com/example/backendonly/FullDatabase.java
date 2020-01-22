package com.example.backendonly;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Term.class, Course.class}, exportSchema = false, version = 1)
public abstract class FullDatabase extends RoomDatabase {
    private static final String DB_NAME = "full_db8";
    private static FullDatabase instance;

    public static synchronized FullDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), FullDatabase.class, DB_NAME).allowMainThreadQueries().build();

        }
        return instance;
    }

    public abstract DatabaseDao databaseDao();
}