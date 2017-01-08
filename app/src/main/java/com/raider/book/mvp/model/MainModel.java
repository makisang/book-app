package com.raider.book.mvp.model;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.raider.book.mvp.contract.MainContract;
import com.raider.book.contract.RaiderDBContract;
import com.raider.book.dao.LocalBook;
import com.raider.book.utils.BookDBOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_LENGTH;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_PATH;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_TITLE;

public class MainModel implements MainContract.Model {
    Context mContext;

    public MainModel(Context context) {
        this.mContext = context;
    }

    public ArrayList<LocalBook> loadFromDB() {
        SQLiteDatabase db = new BookDBOpenHelper(mContext).getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + RaiderDBContract.ShelfReader.TABLE_NAME, null);
        ArrayList<LocalBook> books = new ArrayList<>();
        if (cursor == null) {
            return books;
        }
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
            int length = cursor.getInt(cursor.getColumnIndex(COLUMN_LENGTH));
            books.add(new LocalBook(name, path, length));
        }
        cursor.close();
        db.close();
        return books;
    }

    /**
     * Real action to delete non-existent books.
     *
     * @param currentBooks Books in db before action
     * @return Books deleted if success, null if failed in db delete.
     */
    public ArrayList<LocalBook> deleteNonexistentFromDB(List<LocalBook> currentBooks) {
        ArrayList<LocalBook> deleteBooks = new ArrayList<>();

        File file;
        for (LocalBook book : currentBooks) {
            file = new File(book.path);
            if (!file.exists())
                deleteBooks.add(book);
        }

        if (deleteBooks.size() > 0) {
            SQLiteDatabase db = new BookDBOpenHelper(mContext).getWritableDatabase();
            db.beginTransaction();

            try {
                String[] whereArgs;
                for (LocalBook deleteBook : deleteBooks) {
                    whereArgs = new String[]{deleteBook.path};
                    db.delete(RaiderDBContract.ShelfReader.TABLE_NAME
                            , COLUMN_PATH + "=?"
                            , whereArgs);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                return null;
            } finally {
                db.endTransaction();
            }
        }

        return deleteBooks;
    }

    /**
     * Delete book data from db.
     *
     * @param deleteFiles true: delete files.
     * @return success or failure
     */
    public boolean deleteSelectedBooksFromDB(ArrayList<LocalBook> deleteBooks, boolean deleteFiles) {
        boolean hasException = false;
        SQLiteDatabase db = new BookDBOpenHelper(mContext).getWritableDatabase();
        db.beginTransaction();

        try {
            String[] whereArgs;
            for (LocalBook deleteBook : deleteBooks) {
                whereArgs = new String[]{deleteBook.path};
                db.delete(RaiderDBContract.ShelfReader.TABLE_NAME
                        , COLUMN_PATH + "=?"
                        , whereArgs);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            hasException = true;
        } finally {
            db.endTransaction();
        }

        if (hasException) return false;

        if (deleteFiles) {
            File file;
            for (LocalBook deleteBook : deleteBooks) {
                file = new File(deleteBook.path);
                // Notify MediaStore.
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
                mContext.sendBroadcast(intent);
                if (!file.delete()) {
                    return false;
                }
            }
        }
        return true;
    }

}
