package com.raider.book.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.raider.book.R;

public class SplashActivity extends AppCompatActivity {
    @SuppressWarnings("all")
    private long MINIMUM_STICK_TIME = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShelfActivity.start(SplashActivity.this);
            }
        }, MINIMUM_STICK_TIME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
