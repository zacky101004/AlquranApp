package com.example.alquranapp.network;

import com.example.alquranapp.model.JuzResponse;
import com.example.alquranapp.model.SurahDetailResponse;
import com.example.alquranapp.model.SurahListResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuranApiService {

    @GET("surah")
    Call<SurahListResponse> getAllSurahs();

    @GET("surah/{surahId}/editions/quran-uthmani,ar.alafasy")
    Call<SurahDetailResponse> getSurahDetailWithAudio(@Path("surahId") int surahId);

    @GET("juz/{juzId}/quran-uthmani")
    Call<JuzResponse> getJuz(@Path("juzId") int juzId);

    @GET("surah/{surahId}/editions/id.indonesian")
    Call<SurahDetailResponse> getSurahTranslation(@Path("surahId") int surahId);

    @GET("juzs/{juzId}/quran-uthmani")
    Call<JuzResponse> getJuzWithAudio(@Path("juzId") int juzId);

    @GET("juzs/{juzId}/ar.alafasy")
    Call<JuzResponse> getJuzAudio(@Path("juzId") int juzId);

    @GET("juzs/{juzId}/id.indonesian")
    Call<JuzResponse> getJuzTranslation(@Path("juzId") int juzId);
}