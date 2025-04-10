package com.example.alquranapp.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.alquranapp.R;
import com.example.alquranapp.model.Surah;
import java.util.ArrayList;
import java.util.List;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.ViewHolder> {

    private List<Surah> surahList;
    private List<Surah> originalList;

    public SurahAdapter(List<Surah> surahList) {
        this.surahList = surahList;
        this.originalList = new ArrayList<>(surahList);
    }

    public void filter(String text) {
        surahList.clear();
        if (text.isEmpty()) {
            surahList.addAll(originalList);
        } else {
            for (Surah surah : originalList) {
                if (surah.getNameSimple().toLowerCase().contains(text.toLowerCase())) {
                    surahList.add(surah);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_surah, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Surah surah = surahList.get(position);
        holder.tvName.setText(surah.getNameSimple());
        holder.tvTranslation.setText(surah.getTranslatedName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SurahDetailActivity.class);
            intent.putExtra("surah_id", surah.getId());
            intent.putExtra("surah_name", surah.getNameSimple());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return surahList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTranslation;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTranslation = itemView.findViewById(R.id.tv_translation);
        }
    }
}
