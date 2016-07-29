package com.raider.book.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.raider.book.contract.RaiderDBContract;

public class BookDBOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String BOOK_DB_NAME = "ShelfReader.db";
    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    private static final String BOOK_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + RaiderDBContract.ShelfReader.TABLE_NAME + " (" +
                    RaiderDBContract.ShelfReader._ID + " INTEGER PRIMARY KEY," +
                    RaiderDBContract.ShelfReader.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    RaiderDBContract.ShelfReader.COLUMN_NAME_PATH + TEXT_TYPE + COMMA_SEP +
                    RaiderDBContract.ShelfReader.COLUMN_NAME_SIZE + TEXT_TYPE + COMMA_SEP +
                    RaiderDBContract.ShelfReader.COLUMN_NAME_TIME + TEXT_TYPE +
                    ");";

    public BookDBOpenHelper(Context context) {
        super(context, BOOK_DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BOOK_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE books2 (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, path TEXT, size INTEGER DEFAULT 0, time TEXT DEFAULT '1970-1-1');");
        db.execSQL("INSERT INTO books2 (name, path) SELECT name, path FROM books;");
        db.execSQL("DROP TABLE books;");
        db.execSQL("ALTER TABLE books2 RENAME TO books");
    }
}
