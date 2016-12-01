package com.raider.book.mvp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseIntArray;

import com.raider.book.contract.RaiderDBContract;
import com.raider.book.engine.BookScanner;
import com.raider.book.dao.BookData;
import com.raider.book.mvp.contract.SDImportContract;
import com.raider.book.utils.BookDBOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SmartModel implements SDImportContract.ScannerModel {

    private static final String TAG = "test";

    Context mContext;
    ArrayList<BookData> books;

    public SmartModel(Context context) {
        this.mContext = context;
    }

    @Override
    public ArrayList<BookData> getAllFiles() {
        books = BookScanner.traverseInSD();
        // sort by size descend.
        Collections.sort(books, new Comparator<BookData>() {
            @Override
            public int compare(BookData bookData, BookData t1) {
                return bookData.size > t1.size ? -1 : bookData.size == t1.size ? 0 : 1;
            }
        });
        return books;
    }

    @Override
    public ArrayList<BookData> save2DB(SparseIntArray sparseIntArray) {
        // 用于通知书架更新界面
        ArrayList<BookData> addedBooks = new ArrayList<>();

        ArrayList<ContentValues> insertList = new ArrayList<>();
        SQLiteDatabase db = new BookDBOpenHelper(mContext).getWritableDatabase();
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
            Log.e(TAG, "db transaction fail in ScannerModel");
            addedBooks.clear();
        } finally {
            db.endTransaction();
            db.close();
        }
        return addedBooks;
    }
}
