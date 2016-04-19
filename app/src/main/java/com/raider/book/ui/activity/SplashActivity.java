package com.raider.book.ui.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.raider.book.R;
import com.raider.book.contract.DBConstants;
import com.raider.book.model.entity.BookData;
import com.raider.book.utils.BookDBOpenHelper;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    @SuppressWarnings("all")
    private long MINIMUM_STICK_TIME = 2500;
    private long start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        start = SystemClock.uptimeMillis();
        new AcquireDBTask().execute();
    }

    /**
     * 从数据库查询书架中的书籍
     */
    private class AcquireDBTask extends AsyncTask<Void, Void, ArrayList<BookData>> {
        @Override
        protected ArrayList<BookData> doInBackground(Void... params) {
            SQLiteDatabase db = new BookDBOpenHelper(SplashActivity.this.getApplicationContext()).getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + BookDBOpenHelper.BOOK_TABLE_NAME, null);
            ArrayList<BookData> books = new ArrayList<>();
            if (cursor == null) {
                return books;
            }
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME));
                String path = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_PATH));
                books.add(new BookData(name, path));
            }
            cursor.close();
            return books;
        }

        @Override
        protected void onPostExecute(final ArrayList<BookData> books) {
            super.onPostExecute(books);
            long timePassed = SystemClock.uptimeMillis() - start;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 跳转到书架
                    ShelfActivity.start(SplashActivity.this, books);
                }
            }, MINIMUM_STICK_TIME - timePassed);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
