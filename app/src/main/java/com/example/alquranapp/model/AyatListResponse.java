package com.example.alquranapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AyatListResponse {
    @SerializedName("data")
    private List<SurahData> data;

    public List<SurahData> getData() {
        return data;
    }

    public class SurahData {
        private int number;
        private String name;
        private String englishName;
        private String englishNameTranslation;
        private String revelationType;
        private int numberOfAyahs;

        @SerializedName("ayahs")
        private List<Ayat> ayahs;

        public int getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }

        public String getEnglishName() {
            return englishName;
        }

        public String getEnglishNameTranslation() {
            return englishNameTranslation;
        }

        public String getRevelationType() {
            return revelationType;
        }

        public int getNumberOfAyahs() {
            return numberOfAyahs;
        }

        public List<Ayat> getAyahs() {
            return ayahs;
        }
    }
}