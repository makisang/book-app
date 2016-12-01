package com.raider.book.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.raider.book.R;
import com.raider.book.adapter.MyPagerAdapter;
import com.raider.book.custom.TextFAB;
import com.raider.book.dao.BookData;
import com.raider.book.fragment.FileFragment;
import com.raider.book.fragment.SmartFragment;
import com.raider.book.mvp.model.CPModel;
import com.raider.book.mvp.presenter.SDImportPresenter;

import java.util.ArrayList;
import java.util.List;

public class SDImportActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 213;
    public static final String EXTRA_BOOKS_IN_SHELF = "books_in_shelf";
    private TextFAB fab;
    private SDImportPresenter mPresenter;

    @SuppressWarnings("unchecked")
    public static void start(Activity activity, int requestCode, List<BookData> shelfBooks) {
        Intent intent = new Intent(activity, SDImportActivity.class);
        ArrayList<BookData> newBooks = new ArrayList<>();
        newBooks.addAll(shelfBooks);
        intent.putParcelableArrayListExtra(EXTRA_BOOKS_IN_SHELF, newBooks);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_import);
        initViews();
        customToolBar();

        // RunTime permission request for M
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager
                .PERMISSION_GRANTED) {
            initVPAndTab();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
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

    private void initViews() {
        fab = (TextFAB) findViewById(R.id.my_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addToShelf();
            }
        });
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
        SmartFragment smartFragment = SmartFragment.newInstance(getIntent().<BookData>getParcelableArrayListExtra
                (EXTRA_BOOKS_IN_SHELF));
        FileFragment fileFragment = FileFragment.newInstance();

//        mPresenter = new SDImportPresenter(smartFragment, new SmartModel(getApplicationContext()));
        mPresenter = new SDImportPresenter(smartFragment, new CPModel(getApplicationContext()));

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(smartFragment, "Smart");
        pagerAdapter.addFragment(fileFragment, "File");
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void updateFabNumber(int number) {
        fab.setNumber(number);
    }

    public void enableFab() {
        fab.setEnabled(true);
    }

    public void disableFab() {
        fab.setEnabled(false);
    }

    public void showFab() {
        fab.show();
        fab.setEnabled(true);
    }

    public void hideFab() {
        fab.setEnabled(false);
        fab.hide();
    }

}
