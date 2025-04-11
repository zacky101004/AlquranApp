package com.example.alquranapp.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.alquranapp.model.Bookmark;

import java.util.List;

@Dao
public interface BookmarkDao {

    @Insert
    void insert(Bookmark bookmark);

    @Delete
    void delete(Bookmark bookmark);

    @Query("SELECT * FROM bookmarks")
    List<Bookmark> getAll();

    @Query("SELECT * FROM bookmarks WHERE surahId = :surahId AND ayatNumber = :ayatNumber LIMIT 1")
    Bookmark getBySurahAndAyat(int surahId, int ayatNumber);
}