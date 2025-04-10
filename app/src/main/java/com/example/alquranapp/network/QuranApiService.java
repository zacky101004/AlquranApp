package com.example.alquranapp.network;

import com.example.alquranapp.model.AyatResponse;
import com.example.alquranapp.model.SurahResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface QuranApiService {
    @GET("chapters")
    Call<SurahResponse> getAllSurahs(@Query("language") String lang); // "id" untuk Bahasa Indonesia

    @GET("verses/by_chapter/{id}")
    Call<AyatResponse> getAyatBySurah(
            @Path("id") int surahId,
            @Query("language") String lang,
            @Query("words") boolean withWords
    );

}
