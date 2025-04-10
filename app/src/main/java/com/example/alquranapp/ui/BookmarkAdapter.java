package com.example.alquranapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.alquranapp.R;
import com.example.alquranapp.database.AppDatabase;
import com.example.alquranapp.model.Bookmark;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private List<Bookmark> bookmarkList;

    public BookmarkAdapter(List<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bookmark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bookmark bookmark = bookmarkList.get(position);
        holder.tvVerseNumber.setText("Ayat " + bookmark.getVerseNumber());
        holder.tvArabic.setText(bookmark.getArabicText());
        holder.tvTranslation.setText(bookmark.getTranslation());

        holder.btnDelete.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getInstance(v.getContext());
            db.bookmarkDao().delete(bookmark);

            bookmarkList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, bookmarkList.size());

            Toast.makeText(v.getContext(), "Bookmark dihapus", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVerseNumber, tvArabic, tvTranslation;
        ImageButton btnDelete;

        ViewHolder(View view) {
            super(view);
            tvVerseNumber = view.findViewById(R.id.tv_bookmark_verse_number);
            tvArabic = view.findViewById(R.id.tv_bookmark_arabic);
            tvTranslation = view.findViewById(R.id.tv_bookmark_translation);
            btnDelete = view.findViewById(R.id.btn_delete_bookmark);
        }
    }
}
