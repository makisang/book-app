package com.raider.book.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.raider.book.R;
import com.raider.book.app.BookApplication;
import com.raider.book.dao.BookData;
import com.raider.book.dao.NetBook;
import com.raider.book.fragment.ReadFragment;
import com.raider.book.utils.Utils;
import com.squareup.leakcanary.RefWatcher;

public class ReadActivity extends AppCompatActivity {
    private static final String EXTRA_BOOK_DATA = "intent_book_data";
    public static final String BUNDLE_BOOK_DATA = "bundle_book_data";
    private static final String EXTRA_NET_BOOK = "intent_net_book";

    private String[] fragmentTags = {"ReadFragment"};
    private ReadFragment mFragment;

    @SuppressWarnings("unchecked")
    public static void start(Activity activity, BookData bookData) {
        Intent intent = new Intent(activity, ReadActivity.class);
        intent.putExtra(EXTRA_BOOK_DATA, bookData);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, NetBook netBook) {
        Intent intent = new Intent(activity, ReadActivity.class);
        intent.putExtra(EXTRA_NET_BOOK, netBook);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        Intent intent = getIntent();
        BookData bookData = intent.getParcelableExtra(EXTRA_BOOK_DATA);

        // Add mFragment to activity.
        mFragment = (ReadFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (mFragment == null) {
            mFragment = ReadFragment.newInstance();
            // Send book data to fragment.
            Bundle bundle = new Bundle();
            bundle.putParcelable(BUNDLE_BOOK_DATA, bookData);
            mFragment.setArguments(bundle);
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
