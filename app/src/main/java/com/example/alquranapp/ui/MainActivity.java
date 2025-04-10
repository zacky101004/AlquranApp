package com.example.alquranapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.alquranapp.R;
import com.example.alquranapp.model.Surah;
import com.example.alquranapp.model.SurahResponse;
import com.example.alquranapp.network.ApiClient;
import com.example.alquranapp.network.QuranApiService;
import com.example.alquranapp.notification.DailyReminderWorker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SurahAdapter adapter;
    private SearchView searchView;
    private Button btnViewBookmark;
    private List<Surah> fullSurahList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_surah);
        searchView = findViewById(R.id.search_view);
        btnViewBookmark = findViewById(R.id.btn_view_bookmark);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SurahAdapter(fullSurahList);
        recyclerView.setAdapter(adapter);

        loadSurahs();
        setupSearch();
        scheduleDailyNotification(); // Jadwalkan notifikasi harian

        btnViewBookmark.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
            startActivity(intent);
        });
    }

    private void loadSurahs() {
        QuranApiService api = ApiClient.getClient().create(QuranApiService.class);
        Call<SurahResponse> call = api.getAllSurahs("id");

        call.enqueue(new Callback<SurahResponse>() {
            @Override
            public void onResponse(Call<SurahResponse> call, Response<SurahResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullSurahList.clear();
                    fullSurahList.addAll(response.body().getChapters());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SurahResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal terhubung: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "API Error: ", t);
            }
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Tidak digunakan
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private void scheduleDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long currentTime = System.currentTimeMillis();
        long targetTime = calendar.getTimeInMillis();
        if (targetTime < currentTime) {
            targetTime += 24 * 60 * 60 * 1000;
        }

        long initialDelay = targetTime - currentTime;

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                DailyReminderWorker.class,
                24, TimeUnit.HOURS
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "daily_reminder_work",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );
    }
}
