package com.raider.book.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.raider.book.utils.BookDBOpenHelper;

public class BookApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BookDBOpenHelper openHelper = new BookDBOpenHelper(this);
        openHelper.getWritableDatabase();

        Log.v("test", Environment.getExternalStorageDirectory().getAbsolutePath());
    }
}
