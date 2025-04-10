package com.example.alquranapp.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.alquranapp.R;
import com.example.alquranapp.model.Ayat;
import com.example.alquranapp.model.AyatResponse;
import com.example.alquranapp.network.ApiClient;
import com.example.alquranapp.network.QuranApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurahDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvSurahName;
    private AyatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah_detail);

        int surahId = getIntent().getIntExtra("surah_id", 1);
        String surahName = getIntent().getStringExtra("surah_name");

        tvSurahName = findViewById(R.id.tv_surah_name);
        recyclerView = findViewById(R.id.rv_ayat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvSurahName.setText(surahName);

        QuranApiService api = ApiClient.getClient().create(QuranApiService.class);
        api.getAyatBySurah(surahId, "id", false).enqueue(new Callback<AyatResponse>() {
            @Override
            public void onResponse(Call<AyatResponse> call, Response<AyatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ayat> ayatList = response.body().getVerses();
                    adapter = new AyatAdapter(ayatList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(SurahDetailActivity.this, "Gagal mengambil ayat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AyatResponse> call, Throwable t) {
                Toast.makeText(SurahDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
