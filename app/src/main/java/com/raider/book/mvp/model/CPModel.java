package com.raider.book.mvp.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseIntArray;

import com.raider.book.contract.RaiderDBContract;
import com.raider.book.mvp.contract.SDImportContract;
import com.raider.book.engine.BookScanner;
import com.raider.book.dao.LocalBook;
import com.raider.book.utils.BookDBOpenHelper;

import java.util.ArrayList;

import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_PATH;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_TITLE;

public class CPModel implements SDImportContract.ScannerModel {

    private static final String TAG = "test";

    Context mContext;
    ArrayList<LocalBook> books;

    public CPModel(Context context) {
        mContext = context;
    }

    @Override
    public ArrayList<LocalBook> getAllFiles() {
        books = BookScanner.getFilesFromMediaStore(mContext.getContentResolver());
        return books;
    }

    @Override
    public ArrayList<LocalBook> save2DB(SparseIntArray sparseIntArray) {
        // 用于通知书架更新界面
        ArrayList<LocalBook> addedBooks = new ArrayList<>();

        ArrayList<ContentValues> insertList = new ArrayList<>();
        SQLiteDatabase db = new BookDBOpenHelper(mContext).getWritableDatabase();
        if (books != null) {
            for (int i = 0; i < sparseIntArray.size(); i++) {
                int _i = sparseIntArray.valueAt(i);
                LocalBook book = books.get(_i);
                addedBooks.add(book);
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_TITLE, book.title);
                contentValues.put(COLUMN_PATH, book.path);
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
