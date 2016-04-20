package com.raider.book.model.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseIntArray;

import com.raider.book.contract.RaiderDBContract;
import com.raider.book.engine.ScanSD;
import com.raider.book.event.EventScanSDResult;
import com.raider.book.event.EventUpdateShelf;
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
        ScanSD.shutdown();
    }

    /**
     * 遍历.txt文件
     */
    private class TraverseBookTask extends AsyncTask<Void, Void, ArrayList<BookData>> {
        @Override
        protected ArrayList<BookData> doInBackground(Void... params) {
            books = ScanSD.traverseInSD();
            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<BookData> books) {
            super.onPostExecute(books);
            Log.v(TAG, "TraverseBookTask complete");
            Log.v(TAG, books.toString());
            if (!shutdownRequested) {
                // 通知Presenter遍历的结果
                EventBus.getDefault().post(new EventScanSDResult(books));
            }
        }
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
            EventBus.getDefault().post(new EventUpdateShelf(addedBooks));
        } catch (Exception e) {
            Log.e(TAG, "db transaction fail in BookImportModelImpl");
            addedBooks.clear();
        } finally {
            db.endTransaction();
        }
        return addedBooks;
    }

}
