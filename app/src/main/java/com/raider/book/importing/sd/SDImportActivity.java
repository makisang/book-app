package com.raider.book.importing.sd;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.raider.book.R;
import com.raider.book.VPActivity;
import com.raider.book.adapter.MyPagerAdapter;
import com.raider.book.entity.BookData;

import java.util.ArrayList;
import java.util.List;

public class SDImportActivity extends VPActivity {

    private static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 213;
    public static final String EXTRA_BOOKS_IN_SHELF = "books_in_shelf";

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
        //set up ToolBar
        customToolBar();

        // RunTime permission request for M
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initVPAndTab();
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
                    initVPAndTab();
                break;
        }
    }

    private void customToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @SuppressWarnings("all")
    private void initVPAndTab() {
        ArrayList<BookData> parcelableArrayListExtra = getIntent().<BookData>getParcelableArrayListExtra(EXTRA_BOOKS_IN_SHELF);
        SmartFragment smartFragment = SmartFragment.newInstance(getIntent().<BookData>getParcelableArrayListExtra(EXTRA_BOOKS_IN_SHELF));
        FileFragment fileFragment = FileFragment.newInstance();

        new SmartPresenter(smartFragment, new SmartModel(this.getApplicationContext()));

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(smartFragment, "Smart");
        pagerAdapter.addFragment(fileFragment, "File");
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
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
                clickAddMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * Click on the 'Add' item in menu.
     */
    private void clickAddMenu() {
//        SparseIntArray checkedBooks = adapter.getCheckedBooks();
//        if (checkedBooks == null || checkedBooks.size() == 0) {
//            Snackbar.make(coordinatorLayout, R.string.hint_need_checked, Snackbar.LENGTH_SHORT).show();
//            return;
//        }
//        // update db
//        mPresenter.addToShelf(checkedBooks);
    }

//    @Override
//    public void _handleAddBookSuccess(ArrayList<BookData> addedBooks) {
//        // back to shelf
//        MainActivity.back(this, addedBooks);
//    }
//
//    @Override
//    public void _showBooks(ArrayList<BookData> books) {
////        adapter = new SmartAdapter(this, books, shelfBooks);
////        recyclerView.setAdapter(adapter);
//    }

}
