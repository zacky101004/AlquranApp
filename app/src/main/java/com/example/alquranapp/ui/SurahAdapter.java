package com.example.alquranapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alquranapp.R;
import com.example.alquranapp.model.Surah;

import java.util.List;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.SurahViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Surah surah);
    }

    private List<Surah> surahList;
    private OnItemClickListener listener;

    public SurahAdapter(List<Surah> surahList, OnItemClickListener listener) {
        this.surahList = surahList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SurahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SurahViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_surah, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SurahViewHolder holder, int position) {
        Surah surah = surahList.get(position);
        holder.bind(surah, listener);
    }

    @Override
    public int getItemCount() {
        return surahList.size();
    }

    static class SurahViewHolder extends RecyclerView.ViewHolder {
        TextView tvSurahName, tvTranslation;

        SurahViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSurahName = itemView.findViewById(R.id.tv_name);
            tvTranslation = itemView.findViewById(R.id.tv_translation);
        }

        void bind(Surah surah, OnItemClickListener listener) {
            tvSurahName.setText(surah.getEnglishName());
            tvTranslation.setText(surah.getEnglishNameTranslation());
            itemView.setOnClickListener(v -> listener.onItemClick(surah));
        }
    }
}