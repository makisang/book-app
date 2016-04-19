package com.raider.book.ui.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.raider.book.R;
import com.raider.book.adapter.BookInShelfAdapter;
import com.raider.book.model.entity.BookData;

import java.util.ArrayList;

public class ShelfActivity extends AppCompatActivity {
    private static final String EXTRA_SHELF_BOOKS = "shelfBooks";
    private static final int SPAN_COUNT = 3;

    private RecyclerView recyclerView;

    @SuppressWarnings("unchecked")
    public static void start(Activity activity, ArrayList<BookData> books) {
        Intent intent = new Intent(activity, ShelfActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_SHELF_BOOKS, books);
        ActivityCompat.startActivity(activity, intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);
        getWindow().setAllowEnterTransitionOverlap(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        initViews();

        parseIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shelf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_import:
                // 跳转至导书
                BookImportActivity.start(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
    }

    private void parseIntent(Intent intent) {
        ArrayList<BookData> shelfBooks = intent.getParcelableArrayListExtra(EXTRA_SHELF_BOOKS);
        BookInShelfAdapter adapter = new BookInShelfAdapter(this, shelfBooks);
        recyclerView.setAdapter(adapter);
    }

}
