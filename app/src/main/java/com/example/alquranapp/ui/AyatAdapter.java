package com.example.alquranapp.ui;

import android.content.Context;
import android.media.MediaPlayer;
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

public class AyatAdapter extends RecyclerView.Adapter<AyatAdapter.ViewHolder> {

    private final List<Ayat> ayatList;
    private MediaPlayer mediaPlayer;
    private int currentPlayingPosition = -1;

    public AyatAdapter(List<Ayat> ayatList) {
        this.ayatList = ayatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ayat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ayat ayat = ayatList.get(position);
        Context context = holder.itemView.getContext();
        final int adapterPosition = holder.getBindingAdapterPosition();

        holder.tvNumber.setText(String.valueOf(ayat.getVerseNumber()));
        holder.tvArabic.setText(ayat.getTextArabic());
        holder.tvTranslation.setText(ayat.getTranslation());

        AppDatabase db = AppDatabase.getInstance(context);
        boolean isBookmarked = isAyatBookmarked(db, ayat);

        holder.btnBookmark.setImageResource(isBookmarked ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_border);

        holder.btnBookmark.setOnClickListener(v -> {
            if (isAyatBookmarked(db, ayat)) {
                Bookmark existing = db.bookmarkDao().getBySurahAndAyat(ayat.getSurahId(), ayat.getVerseNumber());
                if (existing != null) {
                    db.bookmarkDao().delete(existing);
                    Toast.makeText(context, "Bookmark dihapus", Toast.LENGTH_SHORT).show();
                    holder.btnBookmark.setImageResource(R.drawable.ic_bookmark_border);
                }
            } else {
                Bookmark bookmark = new Bookmark(
                        ayat.getSurahId(),
                        ayat.getSurahName(),
                        ayat.getVerseNumber(),
                        ayat.getTextArabic(),
                        ayat.getTranslation()
                );
                db.bookmarkDao().insert(bookmark);
                Toast.makeText(context, "Ayat ditambahkan ke bookmark", Toast.LENGTH_SHORT).show();
                holder.btnBookmark.setImageResource(R.drawable.ic_bookmark);
            }
        });

        holder.btnPlay.setImageResource(adapterPosition == currentPlayingPosition && mediaPlayer != null && mediaPlayer.isPlaying()
                ? R.drawable.ic_pause
                : R.drawable.ic_play);

        holder.btnPlay.setOnClickListener(v -> {
            if (adapterPosition == currentPlayingPosition && mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    holder.btnPlay.setImageResource(R.drawable.ic_play);
                    Toast.makeText(context, "Audio dijeda", Toast.LENGTH_SHORT).show();
                } else {
                    mediaPlayer.start();
                    holder.btnPlay.setImageResource(R.drawable.ic_pause);
                    Toast.makeText(context, "Audio dilanjutkan", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    notifyItemChanged(currentPlayingPosition);
                }

                String audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/" + ayat.getVerseNumber() + ".mp3";
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioUrl);
                    mediaPlayer.setOnPreparedListener(mp -> {
                        mp.start();
                        holder.btnPlay.setImageResource(R.drawable.ic_pause);
                        Toast.makeText(context, "Memutar ayat " + ayat.getVerseNumber(), Toast.LENGTH_SHORT).show();
                    });
                    mediaPlayer.setOnCompletionListener(mp -> {
                        holder.btnPlay.setImageResource(R.drawable.ic_play);
                        currentPlayingPosition = -1;
                        mediaPlayer.release();
                        mediaPlayer = null;
                        notifyItemChanged(adapterPosition);
                    });
                    mediaPlayer.prepareAsync();
                    currentPlayingPosition = adapterPosition;
                    notifyDataSetChanged();
                } catch (IOException e) {
                    Toast.makeText(context, "Gagal memutar audio", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ayatList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvArabic, tvTranslation;
        ImageButton btnBookmark, btnPlay;

        ViewHolder(View view) {
            super(view);
            tvNumber = view.findViewById(R.id.tv_number);
            tvArabic = view.findViewById(R.id.tv_arabic);
            tvTranslation = view.findViewById(R.id.tv_translation);
            btnBookmark = view.findViewById(R.id.btn_bookmark);
            btnPlay = view.findViewById(R.id.btn_play);
        }
    }

    private boolean isAyatBookmarked(AppDatabase db, Ayat ayat) {
        return db.bookmarkDao().getBySurahAndAyat(
                ayat.getSurahId(),
                ayat.getVerseNumber()
        ) != null;
    }
}
