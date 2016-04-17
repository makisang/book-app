package com.raider.book.utils;

import android.os.Environment;

/**
 * Created by wkq on 2016/4/12.
 * SDCard related.
 */
public class SDCardUtil {

    public static boolean isSDCardAvail() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

}
