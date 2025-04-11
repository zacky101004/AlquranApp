package com.example.alquranapp.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.alquranapp.model.Bookmark;
import com.example.alquranapp.data.BookmarkDao;

@Database(entities = {Bookmark.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract BookmarkDao bookmarkDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "alquran_db"
            ).build();
        }
        return instance;
    }
}