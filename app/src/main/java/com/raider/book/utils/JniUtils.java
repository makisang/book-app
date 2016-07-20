package com.raider.book.utils;

public class JniUtils {

    public native String storeBooksToDB();

    static {
        System.loadLibrary("SDScanner");
    }

}
