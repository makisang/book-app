package com.raider.book.online;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.raider.book.R;

public class OnlineActivity extends AppCompatActivity {

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, OnlineActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        Toolbar toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initVPAndTab();
    }

    /**
     * Initialize ViewPager and Tab indicator.
     */
    @SuppressWarnings("all")
    private void initVPAndTab() {
        JournalFragment journalFragment = JournalFragment.newInstance();
        new JournalPresenter(journalFragment, new JournalModel(this.getApplicationContext()));

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(journalFragment, "Journals");
        pagerAdapter.addFragment(CollectionFragment.newInstance(), "Collections");
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }
}
