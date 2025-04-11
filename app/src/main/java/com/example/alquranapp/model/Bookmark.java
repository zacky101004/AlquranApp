package com.example.alquranapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "bookmarks")
public class Bookmark {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int surahId;
    @NonNull
    private String surahName;
    private int ayatNumber;
    @NonNull
    private String arabicText;
    @NonNull
    private String translation;

    public Bookmark(int surahId, @NonNull String surahName, int ayatNumber, @NonNull String arabicText, @NonNull String translation) {
        this.surahId = surahId;
        this.surahName = surahName;
        this.ayatNumber = ayatNumber;
        this.arabicText = arabicText;
        this.translation = translation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurahId() {
        return surahId;
    }

    @NonNull
    public String getSurahName() {
        return surahName;
    }

    public int getAyatNumber() {
        return ayatNumber;
    }

    @NonNull
    public String getArabicText() {
        return arabicText;
    }

    @NonNull
    public String getTranslation() {
        return translation;
    }

    public String getVerseNumber() {
        return "Ayat " + ayatNumber;
    }
}