package com.raider.book.contract;

import android.provider.BaseColumns;

/**
 * SQLite各种常量
 */
public final class RaiderDBContract {

    public RaiderDBContract() {
    }

    public static abstract class ShelfReader implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_LENGTH = "length";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_START = "start_point";
        public static final String COLUMN_END = "end_point";
    }

}
