package com.example.alquranapp.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.alquranapp.R;
import com.example.alquranapp.data.AppDatabase;
import com.example.alquranapp.model.Ayat;
import com.example.alquranapp.model.Bookmark;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AyatAdapter extends RecyclerView.Adapter<AyatAdapter.AyatViewHolder> {

    private final List<Ayat> ayatList;
    private final String surahName;
    private final int surahId;
    private MediaPlayer mediaPlayer;
    private AyatListener ayatListener;
    private int currentlyPlayingPosition = -1;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public AyatAdapter(List<Ayat> ayatList, String surahName, int surahId) {
        this.ayatList = ayatList != null ? ayatList : new ArrayList<>();
        this.surahName = surahName != null ? surahName : "Unknown Surah";
        this.surahId = surahId;
        this.mediaPlayer = new MediaPlayer();
    }

    public void setAyatListener(AyatListener listener) {
        this.ayatListener = listener;
    }

    @NonNull
    @Override
    public AyatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ayat, parent, false);
        return new AyatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AyatViewHolder holder, int position) {
        Ayat ayat = ayatList.get(position);
        if (ayat == null) return;

        holder.tvAyatNumber.setText(String.valueOf(ayat.getNumberInSurah()));
        holder.tvArabic.setText(ayat.getText() != null ? ayat.getText() : "Teks tidak tersedia");
        holder.tvTranslation.setText(ayat.getTranslation() != null ? ayat.getTranslation() : "Terjemahan tidak tersedia");

        // Update visibility of play/pause buttons
        int currentPosition = holder.getAdapterPosition();
        if (currentPosition == currentlyPlayingPosition && mediaPlayer.isPlaying()) {
            holder.btnPlay.setVisibility(View.GONE);
            holder.btnPause.setVisibility(View.VISIBLE);
        } else {
            holder.btnPlay.setVisibility(View.VISIBLE);
            holder.btnPause.setVisibility(View.GONE);
        }

        // Play button click listener
        holder.btnPlay.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            // Reset previous playback if any
            if (currentlyPlayingPosition != -1 && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                notifyItemChanged(currentlyPlayingPosition);
            }

            if (ayat.getAudio() == null) {
                holder.showError("Audio tidak tersedia untuk ayat ini.");
                return;
            }

            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(ayat.getAudio());
                mediaPlayer.prepare();
                mediaPlayer.start();
                currentlyPlayingPosition = adapterPosition;
                holder.btnPlay.setVisibility(View.GONE);
                holder.btnPause.setVisibility(View.VISIBLE);

                if (ayatListener != null) {
                    ayatListener.saveLastRead(ayat);
                }

                mediaPlayer.setOnCompletionListener(mp -> {
                    int currentPos = holder.getAdapterPosition();
                    if (currentPos == RecyclerView.NO_POSITION) return;

                    mediaPlayer.reset();
                    currentlyPlayingPosition = -1;
                    holder.btnPlay.setVisibility(View.VISIBLE);
                    holder.btnPause.setVisibility(View.GONE);
                });
            } catch (IOException e) {
                holder.showError("Gagal memutar audio: " + e.getMessage());
            }
        });

        // Pause button click listener
        holder.btnPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                holder.btnPlay.setVisibility(View.VISIBLE);
                holder.btnPause.setVisibility(View.GONE);
            }
        });

        // Bookmark button click listener
        holder.btnBookmark.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setTitle("Simpan ke Bookmark");

            View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_bookmark, null);
            Spinner folderSpinner = dialogView.findViewById(R.id.spinnerFolder);
            EditText newFolderEditText = dialogView.findViewById(R.id.etNewFolder);

            List<String> folders = new ArrayList<>();
            folders.add("Default");
            executorService.execute(() -> {
                List<String> existingFolders = AppDatabase.getInstance(holder.itemView.getContext()).bookmarkDao().getAllFolders();
                if (existingFolders != null) {
                    for (String folder : existingFolders) {
                        if (!folders.contains(folder)) {
                            folders.add(folder);
                        }
                    }
                }
                holder.itemView.post(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(),
                            android.R.layout.simple_spinner_item, folders);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    folderSpinner.setAdapter(adapter);
                });
            });

            builder.setView(dialogView);
            builder.setPositiveButton("Simpan", (dialog, which) -> {
                String selectedFolder = folderSpinner.getSelectedItem() != null ? folderSpinner.getSelectedItem().toString() : "Default";
                String newFolder = newFolderEditText.getText().toString().trim();

                if (!newFolder.isEmpty()) {
                    selectedFolder = newFolder;
                }

                Bookmark bookmark = new Bookmark(surahId, surahName, ayat.getNumberInSurah(),
                        ayat.getText(), ayat.getTranslation() != null ? ayat.getTranslation() : "Terjemahan tidak tersedia");
                bookmark.setFolder(selectedFolder);
                bookmark.setLastRead(false);

                executorService.execute(() -> AppDatabase.getInstance(holder.itemView.getContext()).bookmarkDao().insert(bookmark));
            });
            builder.setNegativeButton("Batal", null);
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return ayatList.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        executorService.shutdown();
    }

    static class AyatViewHolder extends RecyclerView.ViewHolder {
        TextView tvAyatNumber, tvArabic, tvTranslation;
        ImageButton btnPlay, btnPause, btnBookmark;

        AyatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAyatNumber = itemView.findViewById(R.id.tvAyatNumber);
            tvArabic = itemView.findViewById(R.id.tvArabic);
            tvTranslation = itemView.findViewById(R.id.tvTranslation);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnPause = itemView.findViewById(R.id.btnPause);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
        }

        void showError(String message) {
            tvTranslation.setText(message);
            tvTranslation.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    public interface AyatListener {
        void saveLastRead(Ayat ayat);
    }
}
