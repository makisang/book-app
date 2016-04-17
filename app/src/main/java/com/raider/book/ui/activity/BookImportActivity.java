package com.raider.book.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.raider.book.R;
import com.raider.book.adapter.BookOverviewAdapter;
import com.raider.book.model.entity.BookData;
import com.raider.book.presenter.BookImportPresenter;
import com.raider.book.ui.view.IBookImportView;

import java.util.ArrayList;

public class BookImportActivity extends AppCompatActivity implements IBookImportView {
    private static final String TAG = "test";

    private RecyclerView recyclerView;
    private BookImportPresenter presenter;
    private ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_import);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        initViews();

        presenter = new BookImportPresenter(this);
        presenter.startTraverse();
    }

    private void initViews() {
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.my_progress);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showBooks(ArrayList<BookData> books) {
        Log.v(TAG, "showBooks");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BookOverviewAdapter adapter = new BookOverviewAdapter(this, books);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void hideProgress() {
        Log.v(TAG, "hideProgress");
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        recyclerView.setAlpha(0f);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.animate().alpha(1f).setDuration(shortAnimTime).setListener(null);

        progressBar.animate().alpha(0f).setDuration(shortAnimTime).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showProgress() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

}
