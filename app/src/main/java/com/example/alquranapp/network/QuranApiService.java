package com.example.alquranapp.network;

import com.example.alquranapp.model.SurahListResponse;
import com.example.alquranapp.model.AyatListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuranApiService {

    @GET("surah")
    Call<SurahListResponse> getAllSurahs();

    @GET("surah/{id}/editions/quran-uthmani,ms.basmeih,en.sahih")
    Call<AyatListResponse> getSurahDetailWithTranslation(@Path("id") int surahId);
}