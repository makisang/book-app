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
import com.raider.book.event.EventUpdateShelf;
import com.raider.book.model.entity.BookData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class ShelfActivity extends AppCompatActivity {
    private static final String EXTRA_SHELF_BOOKS = "shelfBooks";
    private static final int REQUEST_BOOK_IMPORT = 1;
    private static final int SPAN_COUNT = 3;

    private RecyclerView recyclerView;
    private BookInShelfAdapter adapter;
    private ArrayList<BookData> shelfBooks;

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
        EventBus.getDefault().register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        initViews();

        parseIntent(getIntent());
    }

    private void parseIntent(Intent intent) {
        shelfBooks = intent.getParcelableArrayListExtra(EXTRA_SHELF_BOOKS);
        setAdapter(shelfBooks);
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
                BookImportActivity.start(this, REQUEST_BOOK_IMPORT);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
    }


    private void setAdapter(ArrayList<BookData> books) {
        if (adapter == null) {
            adapter = new BookInShelfAdapter(this, books);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.update(books);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateShelf(EventUpdateShelf eventUpdateShelf) {
        // 收到导书Model层的通知，更新RecyclerView
        shelfBooks.addAll(0, eventUpdateShelf.addedBooks);
        setAdapter(shelfBooks);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
