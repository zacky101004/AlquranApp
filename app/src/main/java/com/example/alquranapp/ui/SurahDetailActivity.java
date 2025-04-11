package com.example.alquranapp.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alquranapp.R;
import com.example.alquranapp.model.Ayat;
import com.example.alquranapp.model.AyatListResponse;
import com.example.alquranapp.network.ApiClient;
import com.example.alquranapp.network.QuranApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurahDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AyatAdapter ayatAdapter;
    private TextView tvSurahName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah_detail);

        tvSurahName = findViewById(R.id.tv_surah_name);
        recyclerView = findViewById(R.id.recyclerViewAyat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int surahId = getIntent().getIntExtra("SURAH_ID", 1);
        String surahName = getIntent().getStringExtra("SURAH_NAME");
        tvSurahName.setText(surahName);

        loadAyats(surahId, surahName);
    }

    private void loadAyats(int surahId, String surahName) {
        QuranApiService service = ApiClient.getClient().create(QuranApiService.class);
        service.getSurahDetailWithTranslation(surahId).enqueue(new Callback<AyatListResponse>() {
            @Override
            public void onResponse(Call<AyatListResponse> call, Response<AyatListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AyatListResponse.SurahData> surahDataList = response.body().getData();
                    if (surahDataList.size() >= 2) {
                        List<Ayat> ayats = surahDataList.get(0).getAyahs(); // Teks Arab (quran-uthmani)
                        List<Ayat> translations = surahDataList.get(1).getAyahs(); // Terjemahan (ms.basmeih)

                        // Gabungkan teks Arab dan terjemahan
                        for (int i = 0; i < ayats.size(); i++) {
                            ayats.get(i).setTranslation(translations.get(i).getText());
                        }

                        ayatAdapter = new AyatAdapter(ayats, surahName, surahId);
                        recyclerView.setAdapter(ayatAdapter);
                    } else {
                        Toast.makeText(SurahDetailActivity.this, "Gagal memuat ayat dan terjemahan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SurahDetailActivity.this, "Gagal memuat ayat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AyatListResponse> call, Throwable t) {
                Toast.makeText(SurahDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}