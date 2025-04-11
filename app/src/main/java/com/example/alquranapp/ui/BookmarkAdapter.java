package com.example.alquranapp.ui;

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
        holder.tvVerseNumber.setText(bookmark.getVerseNumber());
        holder.tvArabic.setText(bookmark.getArabicText());
        holder.tvTranslation.setText(bookmark.getTranslation());

        holder.btnDelete.setOnClickListener(v -> {
            new DeleteBookmarkTask(holder, position).execute();
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

    private class DeleteBookmarkTask extends AsyncTask<Void, Void, Void> {
        private ViewHolder holder;
        private int position;

        DeleteBookmarkTask(ViewHolder holder, int position) {
            this.holder = holder;
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
            Bookmark bookmark = bookmarkList.get(position);
            db.bookmarkDao().delete(bookmark);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            bookmarkList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, bookmarkList.size());
            Toast.makeText(holder.itemView.getContext(), "Bookmark dihapus", Toast.LENGTH_SHORT).show();
        }
    }
}