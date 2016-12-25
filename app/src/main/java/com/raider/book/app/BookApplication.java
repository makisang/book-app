package com.raider.book.app;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.raider.book.utils.AVHelper;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class BookApplication extends Application {

    private static RefWatcher mWatcher;

    public static RefWatcher getWatcher() {
        return mWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWatcher = LeakCanary.install(this);
        // LeanCloud
//        AVHelper.initLeanCloud(this);
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);
    }
}
