package com.raider.book.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.raider.book.contract.DBConstants;

public class BookDBOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String BOOK_TABLE_NAME = "bookToRead";
    private static final String BOOK_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + BOOK_TABLE_NAME +
            "(" +
            "_id integer primary key autoIncrement," +
            DBConstants.COLUMN_NAME + " varchar(20)," +
            DBConstants.COLUMN_PATH + " varchar(20)," +
            DBConstants.COLUMN_TIME + " varchar(20)," +
            DBConstants.COLUMN_SIZE + " varchar(20)" +
            ");";

    public BookDBOpenHelper(Context context) {
        super(context, BOOK_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BOOK_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
