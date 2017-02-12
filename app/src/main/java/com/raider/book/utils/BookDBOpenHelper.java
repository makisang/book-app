package com.raider.book.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.raider.book.contract.RaiderDBContract;

import static android.provider.BaseColumns._ID;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_END;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_LENGTH;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_PATH;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_START;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_TIME;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_TITLE;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.TABLE_NAME;

public class BookDBOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    public static final String BOOK_DB_NAME = "ShelfReader.db";
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";

    private static final String BOOK_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_PATH + TEXT_TYPE + COMMA_SEP +
                    COLUMN_TIME + TEXT_TYPE + " DEFAULT '1970-1-1 00:00:00'" + COMMA_SEP +
                    COLUMN_LENGTH + INT_TYPE + " DEFAULT 0" + COMMA_SEP +
                    COLUMN_START + INT_TYPE + " DEFAULT 0" + COMMA_SEP +
                    COLUMN_END + INT_TYPE + " DEFAULT 0" +
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
        db.execSQL("CREATE TABLE books2 (_id INTEGER PRIMARY KEY AUTOINCREMENT, start_point INTEGER DEFAULT 0, end_point INTEGER DEFAULT 0, " +
                "title TEXT, path TEXT, length INTEGER DEFAULT 0, time TEXT DEFAULT '1970-1-1 00:00:00)');");
        db.execSQL("INSERT INTO books2 (title, path) SELECT title, path FROM books;");
        db.execSQL("DROP TABLE books;");
        db.execSQL("ALTER TABLE books2 RENAME TO books");
    }
}
