package com.raider.book.model.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseIntArray;

import com.raider.book.contract.RaiderDBContract;
import com.raider.book.engine.BookScanner;
import com.raider.book.model.IBookImportModel;
import com.raider.book.model.entity.BookData;
import com.raider.book.utils.BookDBOpenHelper;

import java.util.ArrayList;

public class BookImportModelImpl implements IBookImportModel {
    private static final String TAG = "test";

    ArrayList<BookData> books;

    @Override
    public ArrayList<BookData> traverse() {
        books = BookScanner.traverseInSD();
        return books;
    }

    @Override
    public ArrayList<BookData> save2DB(Context context, SparseIntArray sparseIntArray) {
        // 用于通知书架更新界面
        ArrayList<BookData> addedBooks = new ArrayList<>();

        ArrayList<ContentValues> insertList = new ArrayList<>();
        SQLiteDatabase db = new BookDBOpenHelper(context).getWritableDatabase();
        if (books != null) {
            for (int i = 0; i < sparseIntArray.size(); i++) {
                int _i = sparseIntArray.valueAt(i);
                BookData book = books.get(_i);
                addedBooks.add(book);
                ContentValues contentValues = new ContentValues();
                contentValues.put(RaiderDBContract.ShelfReader.COLUMN_NAME_NAME, book.name);
                contentValues.put(RaiderDBContract.ShelfReader.COLUMN_NAME_PATH, book.path);
                insertList.add(contentValues);
            }
        }

        db.beginTransaction();
        try {
            for (ContentValues contentValues : insertList) {
                db.insert(RaiderDBContract.ShelfReader.TABLE_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "db transaction fail in BookImportModelImpl");
            addedBooks.clear();
        } finally {
            db.endTransaction();
        }
        return addedBooks;
    }

}
