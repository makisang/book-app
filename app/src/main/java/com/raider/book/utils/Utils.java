package com.raider.book.utils;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.raider.book.R;

import java.net.ConnectException;

public class Utils {

    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int frameId, String tag) {
        fragmentManager.beginTransaction().add(frameId, fragment, tag).commit();
    }

    public static void showErrorMessage(Context context, Throwable throwable) {
        String message = context.getString(R.string.error);
        if (throwable instanceof ConnectException) {
            message = context.getString(R.string.network_connect_error);
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Check SDK >= 21 or not.
     */
    public static boolean isOrAfterLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }

}
