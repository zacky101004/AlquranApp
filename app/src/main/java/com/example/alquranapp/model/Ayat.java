package com.example.alquranapp.model;

public class Ayat {
    private int numberInSurah;
    private String text;
    private Translation translation;
    private Surah surah;
    private String audio;

    // Getter untuk nomor ayat dalam surah
    public int getVerseNumber() {
        return numberInSurah;
    }

    // Getter untuk teks Arab
    public String getTextArabic() {
        return text;
    }

    // Getter untuk terjemahan
    public String getTranslation() {
        return translation != null ? translation.getText() : "";
    }

    // Getter untuk nama surah
    public String getSurahName() {
        return surah != null ? surah.getName() : "";
    }

    // Getter untuk ID/nama surah
    public int getSurahId() {
        return surah != null ? surah.getNumber() : 0;
    }

    // Getter untuk URL audio
    public String getAudio() {
        return audio;
    }

    // Class untuk data translation
    public static class Translation {
        private String text;

        public String getText() {
            return text;
        }
    }

    // Class untuk informasi surah
    public static class Surah {
        private int number;
        private String name;

        public int getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }
    }
}
