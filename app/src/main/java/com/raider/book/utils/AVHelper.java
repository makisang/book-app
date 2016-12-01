package com.raider.book.utils;

import android.content.Context;

import com.avos.avoscloud.AVOSCloud;

/**
 * LeanCloud initialize.
 */

public class AVHelper {

    public static void initLeanCloud(Context context) {
        AVOSCloud.initialize(context,"12nkM3aFGoKlYTWU2Su6wI1H-9Nh9j0Va","XKbvPTg3ltlIA3YsV5rYSa5X");
    }

}
