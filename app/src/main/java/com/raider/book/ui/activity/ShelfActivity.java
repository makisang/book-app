package com.raider.book.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.raider.book.R;
import com.raider.book.adapter.BookInShelfAdapter;
import com.raider.book.model.entity.BookData;
import com.raider.book.presenter.ShelfPresenter;
import com.raider.book.ui.view.IShelfView;
import com.raider.book.widget.OffsetDecoration;

import java.util.ArrayList;

public class ShelfActivity extends AppCompatActivity implements IShelfView {
    private static final int REQUEST_BOOK_IMPORT = 1;

    private RecyclerView recyclerView;
    private BookInShelfAdapter adapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private ContentLoadingProgressBar progressBar;
    private ShelfPresenter presenter;

    private ArrayList<BookData> shelfBooks;

    @SuppressWarnings("unchecked")
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, ShelfActivity.class);
        ActivityCompat.startActivity(activity, intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);
        getWindow().setAllowEnterTransitionOverlap(true);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        customDrawer();
        initViews();

        presenter = new ShelfPresenter(this.getApplicationContext(), this);
        presenter.loadBooks();
    }

    private void initViews() {
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.my_progress);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler);
        recyclerView.addItemDecoration(new OffsetDecoration(getResources().getDimensionPixelSize(R.dimen.spacing_nano)));
    }

    private void customDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void updateBookGrid(ArrayList<BookData> books) {
        setAdapter(books);
    }

    @Override
    public void showProgress() {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        progressBar.setAlpha(0f);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate().alpha(1f).setDuration(shortAnimTime).setListener(null);

        recyclerView.animate().alpha(0f).setDuration(shortAnimTime).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void hideProgress() {
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
                BookImportActivity.start(this, REQUEST_BOOK_IMPORT, shelfBooks);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAdapter(ArrayList<BookData> books) {
        shelfBooks = books;
        if (adapter == null) {
            adapter = new BookInShelfAdapter(this, books);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.update(books);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BOOK_IMPORT && data != null) {
            presenter.loadBooks();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawerLayout.removeDrawerListener(drawerToggle);
    }


}
