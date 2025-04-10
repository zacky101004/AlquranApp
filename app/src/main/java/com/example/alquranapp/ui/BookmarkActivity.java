package com.example.alquranapp.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.alquranapp.R;
import com.example.alquranapp.database.AppDatabase;
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
        AppDatabase db = AppDatabase.getInstance(this);
        List<Bookmark> bookmarkList = db.bookmarkDao().getAllBookmarks();

        if (bookmarkList.isEmpty()) {
            Toast.makeText(this, "Belum ada bookmark", Toast.LENGTH_SHORT).show();
        }

        bookmarkAdapter = new BookmarkAdapter(bookmarkList);
        rvBookmark.setAdapter(bookmarkAdapter);
    }
}
