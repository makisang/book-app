package com.raider.book.model.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseIntArray;

import com.raider.book.contract.DBConstants;
import com.raider.book.engine.TraverseBook;
import com.raider.book.event.TraverseBookResult;
import com.raider.book.model.IBookImportModel;
import com.raider.book.model.entity.BookData;
import com.raider.book.utils.BookDBOpenHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class BookImportModelImpl implements IBookImportModel {
    private static final String TAG = "test";

    private volatile boolean shutdownRequested = false;
    ArrayList<BookData> books;

    @Override
    public void traverse() {
        new TraverseBookTask().execute();
    }

    @Override
    public void stopTraverse() {
        shutdownRequested = true;
        TraverseBook.shutdown();
    }

    /**
     * 遍历.txt文件
     */
    private class TraverseBookTask extends AsyncTask<Void, Void, ArrayList<BookData>> {
        @Override
        protected ArrayList<BookData> doInBackground(Void... params) {
            books = TraverseBook.traverseInSD();
            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<BookData> books) {
            super.onPostExecute(books);
            Log.v(TAG, "TraverseBookTask complete");
            Log.v(TAG, books.toString());
            if (!shutdownRequested) {
                // 通知Presenter遍历的结果
                EventBus.getDefault().post(new TraverseBookResult(books));
            }
        }
    }

    @Override
    public void save2DB(Context context, SparseIntArray sparseIntArray) {
        BookDBOpenHelper openHelper = new BookDBOpenHelper(context);
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ArrayList<ContentValues> insertList = new ArrayList<>();
        if (books != null) {
            for (int i = 0; i < sparseIntArray.size(); i++) {
                int _i = sparseIntArray.valueAt(i);
                BookData book = books.get(_i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBConstants.COLUMN_NAME, book.name);
                contentValues.put(DBConstants.COLUMN_PATH, book.path);
                insertList.add(contentValues);
            }
        }

        db.beginTransaction();
        try {
            for (ContentValues contentValues : insertList) {
                db.insert(BookDBOpenHelper.BOOK_TABLE_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "db transaction fail in BookImportModelImpl");
        } finally {
            db.endTransaction();
        }
    }

}
