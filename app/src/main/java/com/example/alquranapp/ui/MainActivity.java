package com.example.alquranapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.alquranapp.R;
import com.example.alquranapp.model.Surah;
import com.example.alquranapp.model.SurahListResponse;
import com.example.alquranapp.network.ApiClient;
import com.example.alquranapp.network.QuranApiService;
import com.example.alquranapp.notification.DailyReminderWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SurahAdapter adapter;
    private List<Surah> surahList;
    private List<Surah> filteredSurahList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewSurah);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnViewBookmarks = findViewById(R.id.btn_view_bookmarks);
        btnViewBookmarks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
            startActivity(intent);
        });

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSurahs(newText);
                return true;
            }
        });

        // Cek izin notifikasi untuk Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        // Jadwalkan notifikasi harian
        scheduleDailyReminder();

        loadSurahs();
    }

    private void loadSurahs() {
        QuranApiService service = ApiClient.getClient().create(QuranApiService.class);
        service.getAllSurahs().enqueue(new Callback<SurahListResponse>() {
            @Override
            public void onResponse(Call<SurahListResponse> call, Response<SurahListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    surahList = response.body().getData();
                    filteredSurahList = new ArrayList<>(surahList);
                    adapter = new SurahAdapter(filteredSurahList, surah -> {
                        Intent intent = new Intent(MainActivity.this, SurahDetailActivity.class);
                        intent.putExtra("SURAH_ID", surah.getNumber());
                        intent.putExtra("SURAH_NAME", surah.getEnglishName());
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Gagal memuat Surah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SurahListResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterSurahs(String query) {
        filteredSurahList.clear();
        if (query.isEmpty()) {
            filteredSurahList.addAll(surahList);
        } else {
            for (Surah surah : surahList) {
                if (surah.getEnglishName().toLowerCase().contains(query.toLowerCase()) ||
                        surah.getEnglishNameTranslation().toLowerCase().contains(query.toLowerCase())) {
                    filteredSurahList.add(surah);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void scheduleDailyReminder() {
        PeriodicWorkRequest dailyWorkRequest =
                new PeriodicWorkRequest.Builder(DailyReminderWorker.class, 1, TimeUnit.DAYS)
                        .build();
        WorkManager.getInstance(this).enqueue(dailyWorkRequest);
    }
}