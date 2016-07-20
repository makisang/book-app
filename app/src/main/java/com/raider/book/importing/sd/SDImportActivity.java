package com.raider.book.importing.sd;

import android.Manifest;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.raider.book.R;
import com.raider.book.adapter.BookInSDAdapter;
import com.raider.book.entity.BookData;
import com.raider.book.home.MainActivity;
import com.raider.book.utils.CustomAnim;

import java.util.ArrayList;
import java.util.List;

public class SDImportActivity extends AppCompatActivity implements SDImportContract.View {

    private static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 213;
    private static final String EXTRA_BOOKS_IN_SHELF = "books_in_shelf";

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private SDImportPresenter mPresenter;
    private ContentLoadingProgressBar progressBar;
    private BookInSDAdapter adapter;
    private ArrayList<BookData> shelfBooks;

    @SuppressWarnings("unchecked")
    public static void start(Activity activity, int requestCode, List<BookData> shelfBooks) {
        Intent intent = new Intent(activity, SDImportActivity.class);
        ArrayList<BookData> newBooks = new ArrayList<>();
        newBooks.addAll(shelfBooks);
        intent.putParcelableArrayListExtra(EXTRA_BOOKS_IN_SHELF, newBooks);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_import);

        //set up ToolBar
        customToolBar();

        initViews();

        shelfBooks = getIntent().getParcelableArrayListExtra(EXTRA_BOOKS_IN_SHELF);

        new SDImportPresenter(this, new SDImportModel(getApplicationContext()));

        // RunTime permission request for M
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mPresenter.start();
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
                    mPresenter.start();
                break;
        }
    }

    private void customToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.my_coordinator);
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.my_progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.GONE);
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

    private void addBooks() {
        SparseIntArray checkedBooks = adapter.getCheckedBooks();
        if (checkedBooks == null || checkedBooks.size() == 0) {
            Snackbar.make(coordinatorLayout, R.string.hint_need_checked, Snackbar.LENGTH_SHORT).show();
            return;
        }
        // update db
        mPresenter.addToShelf(checkedBooks);
    }

    @Override
    public void _handleAddBookSuccess(ArrayList<BookData> addedBooks) {
        // back to shelf
        MainActivity.back(this, addedBooks);
    }

    @Override
    public void _showBooks(ArrayList<BookData> books) {
        adapter = new BookInSDAdapter(this, books, shelfBooks);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void _hideProgress() {
        CustomAnim.hideProgress(getApplicationContext(), recyclerView, progressBar);
    }

    @Override
    public void _showProgress() {
        CustomAnim.showProgress(getApplicationContext(), recyclerView, progressBar);
    }

    @Override
    public void _setPresenter(SDImportPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

}
