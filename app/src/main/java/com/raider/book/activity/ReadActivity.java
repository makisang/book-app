package com.raider.book.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.raider.book.R;
import com.raider.book.app.BookApplication;
import com.raider.book.dao.Book;
import com.raider.book.dao.LocalBook;
import com.raider.book.dao.NetBook;
import com.raider.book.fragment.ReadFragment;
import com.raider.book.utils.Utils;
import com.squareup.leakcanary.RefWatcher;

public class ReadActivity extends AppCompatActivity {
    public static final String EXTRA_BOOK = "intent_book";

    private String[] fragmentTags = {"ReadFragment"};
    private ReadFragment mFragment;


    public static void start(Activity activity, Book book) {
        Intent intent = new Intent(activity, ReadActivity.class);
        intent.putExtra(EXTRA_BOOK, book);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FullScreen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_read);

        Intent intent = getIntent();
        Book book = intent.getParcelableExtra(EXTRA_BOOK);

        mFragment = (ReadFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (mFragment == null) {
            mFragment = ReadFragment.newInstance(book);
            Utils.addFragmentToActivity(getSupportFragmentManager(), mFragment, R.id.frame_container, fragmentTags[0]);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher watcher = BookApplication.getWatcher();
        watcher.watch(this);
    }
}
