package com.example.alquranapp.model;

import com.google.gson.annotations.SerializedName;

public class Ayat {

    @SerializedName("numberInSurah")
    private int numberInSurah;

    @SerializedName("text")
    private String text;

    @SerializedName("audio")
    private String audio; // URL audio untuk murottal

    private String translation; // Terjemahan (akan diisi manual dari response)

    public int getNumberInSurah() {
        return numberInSurah;
    }

    public String getText() {
        return text;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getAudio() {
        return audio;
    }
}