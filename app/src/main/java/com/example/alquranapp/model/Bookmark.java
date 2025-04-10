package com.example.alquranapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookmarks")
public class Bookmark {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int surahId;
    private String surahName;
    private int ayatNumber;
    private String arabicText;
    private String translation;

    public Bookmark(int surahId, String surahName, int ayatNumber, String arabicText, String translation) {
        this.surahId = surahId;
        this.surahName = surahName;
        this.ayatNumber = ayatNumber;
        this.arabicText = arabicText;
        this.translation = translation;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSurahId() { return surahId; }
    public String getSurahName() { return surahName; }
    public int getAyatNumber() { return ayatNumber; }
    public String getArabicText() { return arabicText; }
    public String getTranslation() { return translation; }
}
