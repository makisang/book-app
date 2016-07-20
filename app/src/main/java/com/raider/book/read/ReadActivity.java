package com.raider.book.read;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.raider.book.R;
import com.raider.book.entity.BookData;
import com.raider.book.utils.ActivityUtils;

public class ReadActivity extends AppCompatActivity {
    private static final String EXTRA_BOOK_DATA = "intent_book_data";
    public static final String BUNDLE_BOOK_DATA = "bundle_book_data";

    private String[] fragmentTags = {"ReadFragment"};

    @SuppressWarnings("unchecked")
    public static void start(Activity activity, BookData bookData) {
        Intent intent = new Intent(activity, ReadActivity.class);
        intent.putExtra(EXTRA_BOOK_DATA, bookData);
        ActivityCompat.startActivity(activity, intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        Intent intent = getIntent();
        BookData bookData = intent.getParcelableExtra(EXTRA_BOOK_DATA);

        // add mFragment to activity
        ReadFragment mFragment = (ReadFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (mFragment == null) {
            mFragment = ReadFragment.newInstance();
            // set book data to fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable(BUNDLE_BOOK_DATA, bookData);
            mFragment.setArguments(bundle);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mFragment, R.id.frame_container, fragmentTags[0]);
        }
    }
}
