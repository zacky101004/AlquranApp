package com.example.alquranapp.ui;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alquranapp.R;
import com.example.alquranapp.data.AppDatabase;
import com.example.alquranapp.model.Ayat;
import com.example.alquranapp.model.Bookmark;

import java.io.IOException;
import java.util.List;

public class AyatAdapter extends RecyclerView.Adapter<AyatAdapter.AyatViewHolder> {

    private List<Ayat> ayatList;
    private String surahName;
    private int surahId;
    private MediaPlayer mediaPlayer;

    public AyatAdapter(List<Ayat> ayatList, String surahName, int surahId) {
        this.ayatList = ayatList;
        this.surahName = surahName;
        this.surahId = surahId;
        this.mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public AyatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AyatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ayat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AyatViewHolder holder, int position) {
        Ayat ayat = ayatList.get(position);
        holder.tvArabic.setText(ayat.getText());
        holder.tvNumber.setText("Ayat " + ayat.getNumberInSurah());
        holder.tvTranslation.setText(ayat.getTranslation() != null ? ayat.getTranslation() : "Terjemahan tidak tersedia");

        // Cek apakah ayat ini sudah di-bookmark
        new CheckBookmarkTask(holder, ayat).execute();

        // Tombol Bookmark
        holder.btnBookmark.setOnClickListener(v -> {
            new ToggleBookmarkTask(holder, ayat).execute();
        });

        // Tombol Play Audio
        holder.btnPlay.setOnClickListener(v -> {
            if (ayat.getAudio() != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    holder.btnPlay.setImageResource(R.drawable.ic_play);
                } else {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(ayat.getAudio());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        holder.btnPlay.setImageResource(R.drawable.ic_pause);
                        mediaPlayer.setOnCompletionListener(mp -> {
                            holder.btnPlay.setImageResource(R.drawable.ic_play);
                            mediaPlayer.reset();
                        });
                    } catch (IOException e) {
                        Toast.makeText(v.getContext(), "Gagal memutar audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(v.getContext(), "Audio tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ayatList.size();
    }

    static class AyatViewHolder extends RecyclerView.ViewHolder {
        TextView tvArabic, tvNumber, tvTranslation;
        ImageButton btnPlay, btnBookmark;

        AyatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvArabic = itemView.findViewById(R.id.tv_arabic); // Ubah ke tv_arabic
            tvNumber = itemView.findViewById(R.id.tv_number); // Ubah ke tv_number
            tvTranslation = itemView.findViewById(R.id.tv_translation);
            btnPlay = itemView.findViewById(R.id.btn_play);
            btnBookmark = itemView.findViewById(R.id.btn_bookmark);
        }
    }

    private class CheckBookmarkTask extends AsyncTask<Void, Void, Bookmark> {
        private AyatViewHolder holder;
        private Ayat ayat;

        CheckBookmarkTask(AyatViewHolder holder, Ayat ayat) {
            this.holder = holder;
            this.ayat = ayat;
        }

        @Override
        protected Bookmark doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
            return db.bookmarkDao().getBySurahAndAyat(surahId, ayat.getNumberInSurah());
        }

        @Override
        protected void onPostExecute(Bookmark bookmark) {
            holder.btnBookmark.setImageResource(bookmark != null ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_border);
            holder.btnBookmark.setTag(bookmark); // Simpan bookmark di tag untuk digunakan saat toggle
        }
    }

    private class ToggleBookmarkTask extends AsyncTask<Void, Void, Boolean> {
        private AyatViewHolder holder;
        private Ayat ayat;

        ToggleBookmarkTask(AyatViewHolder holder, Ayat ayat) {
            this.holder = holder;
            this.ayat = ayat;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
            Bookmark existingBookmark = (Bookmark) holder.btnBookmark.getTag();
            if (existingBookmark == null) {
                Bookmark bookmark = new Bookmark(
                        surahId,
                        surahName,
                        ayat.getNumberInSurah(),
                        ayat.getText(),
                        ayat.getTranslation() != null ? ayat.getTranslation() : "Terjemahan tidak tersedia"
                );
                db.bookmarkDao().insert(bookmark);
                return true;
            } else {
                db.bookmarkDao().delete(existingBookmark);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isBookmarked) {
            holder.btnBookmark.setImageResource(isBookmarked ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_border);
            holder.btnBookmark.setTag(isBookmarked ? new Bookmark(surahId, surahName, ayat.getNumberInSurah(), ayat.getText(), ayat.getTranslation()) : null);
            Toast.makeText(holder.itemView.getContext(), isBookmarked ? "Ayat ditambahkan ke bookmark" : "Ayat dihapus dari bookmark", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}