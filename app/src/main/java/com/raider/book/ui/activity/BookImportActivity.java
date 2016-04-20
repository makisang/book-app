package com.raider.book.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.raider.book.R;
import com.raider.book.adapter.BookOverviewAdapter;
import com.raider.book.model.entity.BookData;
import com.raider.book.presenter.BookImportPresenter;
import com.raider.book.ui.view.IBookImportView;

import java.util.ArrayList;

public class BookImportActivity extends AppCompatActivity implements IBookImportView {
    private static final String TAG = "test";
    private static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 213;

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private BookImportPresenter presenter;
    private ContentLoadingProgressBar progressBar;
    private BookOverviewAdapter adapter;

    @SuppressWarnings("unchecked")
    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, BookImportActivity.class);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_import);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        initViews();

        presenter = new BookImportPresenter(this.getApplicationContext(), this);

        // 6.0的运行时权限检查
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            presenter.startTraverse();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    presenter.startTraverse();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_import, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                addBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.my_coordinator);
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.my_progress);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showBooks(ArrayList<BookData> books) {
        Log.v(TAG, "showBooks");
        adapter = new BookOverviewAdapter(this, books);
        recyclerView.setAdapter(adapter);
    }

    private void addBooks() {
        SparseIntArray checkedBooks = adapter.getCheckedBooks();
        if (checkedBooks == null || checkedBooks.size() == 0) {
            Snackbar.make(coordinatorLayout, R.string.hint_need_checked, Snackbar.LENGTH_SHORT).show();
            return;
        }
        // Model层更新数据库
        presenter.addToShelf(checkedBooks);
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
    public void showSuccessHint(ArrayList<BookData> addedBooks) {
        String hint = String.format(getResources().getString(R.string.hint_add_book_success), addedBooks.size());
        Snackbar.make(coordinatorLayout, hint, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

}
