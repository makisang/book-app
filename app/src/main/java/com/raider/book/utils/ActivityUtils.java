package com.raider.book.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class ActivityUtils {

    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int frameId, String tag) {
        fragmentManager.beginTransaction().add(frameId, fragment, tag).commit();
    }

}
