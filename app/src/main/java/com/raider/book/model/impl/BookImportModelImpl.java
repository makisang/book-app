package com.raider.book.model.impl;

import android.os.AsyncTask;
import android.util.Log;

import com.raider.book.engine.TraverseBook;
import com.raider.book.event.TraverseBookResult;
import com.raider.book.model.IBookImportModel;
import com.raider.book.model.entity.BookData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class BookImportModelImpl implements IBookImportModel {
    private static final String TAG = "test";

    private volatile boolean shutdownRequested = false;

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
            ArrayList<BookData> books = TraverseBook.traverseInSD();
            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<BookData> books) {
            super.onPostExecute(books);
            Log.v(TAG, "TraverseBookTask complete");
            if (!shutdownRequested) {
                // 给Presenter遍历的结果
                EventBus.getDefault().post(new TraverseBookResult(books));
            }
        }
    }

}
