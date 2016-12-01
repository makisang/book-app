package com.raider.book.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.raider.book.R;
import com.raider.book.adapter.SectionAdapter;
import com.raider.book.custom.CustomRecyclerView;
import com.raider.book.widget.OffsetDecoration;

import java.util.ArrayList;
import java.util.List;

public class SectionActivity extends AppCompatActivity {

    private CustomRecyclerView recyclerView;
    private Resources mResources;
    private CoordinatorLayout.LayoutParams mLayoutParams;
    private int mHeightPixels;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout ctb;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SectionActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        mResources = getResources();

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ctb = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ctb.setCollapsedTitleTextColor(Color.WHITE);
        ctb.setExpandedTitleColor(Color.WHITE);
        ctb.setTitle(mResources.getString(R.string.toolbar_section));

        final Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.mipmap.dnf);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int mutedColor = palette.getMutedColor(mResources.getColor(R.color.light_blue_colorPrimary));
                ctb.setContentScrimColor(mutedColor);
                ctb.setStatusBarScrimColor(mutedColor);
                bitmap.recycle();
            }
        });
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mHeightPixels = mResources.getDisplayMetrics().heightPixels;
        mLayoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeightPixels / 3);
        appBarLayout.setLayoutParams(mLayoutParams);

        recyclerView = (CustomRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new OffsetDecoration(mResources.getDimensionPixelSize(R.dimen.spacing_mid)));
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add("第" + i);
        }
        SectionAdapter sectionAdapter = new SectionAdapter(this, list);
        recyclerView.setAdapter(sectionAdapter);
        recyclerView.setAppBarLayout(appBarLayout, mHeightPixels / 3);
    }

}
