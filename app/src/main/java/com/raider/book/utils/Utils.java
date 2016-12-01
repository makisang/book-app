package com.raider.book.utils;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class Utils {

    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int frameId, String tag) {
        fragmentManager.beginTransaction().add(frameId, fragment, tag).commit();
    }

    /**
     * Check SDK >= 21 or not.
     */
    public static boolean isOrAfterLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }

}
