package com.example.alquranapp.ui.bookmark;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.alquranapp.R;
import com.example.alquranapp.data.AppDatabase;
import com.example.alquranapp.model.Bookmark;
import com.example.alquranapp.ui.BookmarkAdapter;

import java.util.List;

public class BookmarkActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        RecyclerView rv = findViewById(R.id.rv_bookmark);
        List<Bookmark> bookmarks = AppDatabase.getInstance(this).bookmarkDao().getAllBookmarks();
        rv.setAdapter(new BookmarkAdapter(bookmarks));
    }
}
