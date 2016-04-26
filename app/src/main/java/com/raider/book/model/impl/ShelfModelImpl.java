package com.raider.book.model.impl;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.raider.book.contract.RaiderDBContract;
import com.raider.book.model.IShelfModel;
import com.raider.book.model.entity.BookData;
import com.raider.book.utils.BookDBOpenHelper;

import java.util.ArrayList;

public class ShelfModelImpl implements IShelfModel {
    Context mContext;

    public ShelfModelImpl(Context context) {
        this.mContext = context;
    }

    public ArrayList<BookData> loadBooksInDB() {
        SQLiteDatabase db = new BookDBOpenHelper(mContext).getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + RaiderDBContract.ShelfReader.TABLE_NAME, null);
        ArrayList<BookData> books = new ArrayList<>();
        if (cursor == null) {
            return books;
        }
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(RaiderDBContract.ShelfReader.COLUMN_NAME_NAME));
            String path = cursor.getString(cursor.getColumnIndex(RaiderDBContract.ShelfReader.COLUMN_NAME_PATH));
            books.add(new BookData(name, path));
        }
        cursor.close();
        return books;
    }

}
