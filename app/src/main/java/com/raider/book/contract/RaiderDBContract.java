package com.raider.book.contract;

import android.provider.BaseColumns;

/**
 * SQLite各种常量
 */
public final class RaiderDBContract {

    public RaiderDBContract(){}

    public static abstract class ShelfReader implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PATH = "path";
        public static final String COLUMN_NAME_SIZE = "size";
        public static final String COLUMN_NAME_TIME = "time";
    }

}
