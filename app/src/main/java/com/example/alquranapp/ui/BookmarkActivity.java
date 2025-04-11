package com.example.alquranapp.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.alquranapp.R;
import com.example.alquranapp.data.AppDatabase;
import com.example.alquranapp.model.Bookmark;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView rvBookmark;
    private BookmarkAdapter bookmarkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        rvBookmark = findViewById(R.id.rv_bookmark);
        rvBookmark.setLayoutManager(new LinearLayoutManager(this));

        loadBookmarks();
    }

    private void loadBookmarks() {
        new LoadBookmarksTask().execute();
    }

    private class LoadBookmarksTask extends AsyncTask<Void, Void, List<Bookmark>> {
        @Override
        protected List<Bookmark> doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getInstance(BookmarkActivity.this);
            return db.bookmarkDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Bookmark> bookmarkList) {
            if (bookmarkList.isEmpty()) {
                Toast.makeText(BookmarkActivity.this, "Belum ada bookmark", Toast.LENGTH_SHORT).show();
            }
            bookmarkAdapter = new BookmarkAdapter(bookmarkList);
            rvBookmark.setAdapter(bookmarkAdapter);
        }
    }
}