package com.raider.book.mvp.model;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.raider.book.mvp.contract.MainContract;
import com.raider.book.contract.RaiderDBContract;
import com.raider.book.dao.BookData;
import com.raider.book.utils.BookDBOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainModel implements MainContract.Model {
    Context mContext;

    public MainModel(Context context) {
        this.mContext = context;
    }

    public ArrayList<BookData> loadFromDB() {
        SQLiteDatabase db = new BookDBOpenHelper(mContext).getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + RaiderDBContract.ShelfReader.TABLE_NAME, null);
        ArrayList<BookData> books = new ArrayList<>();
        if (cursor == null) {
            return books;
        }
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(RaiderDBContract.ShelfReader.COLUMN_NAME_NAME));
            String path = cursor.getString(cursor.getColumnIndex(RaiderDBContract.ShelfReader.COLUMN_NAME_PATH));
            int size = cursor.getInt(cursor.getColumnIndex(RaiderDBContract.ShelfReader.COLUMN_NAME_SIZE));
            books.add(new BookData(name, path, size));
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
    public ArrayList<BookData> deleteNonexistentFromDB(List<BookData> currentBooks) {
        ArrayList<BookData> deleteBooks = new ArrayList<>();

        File file;
        for (BookData book : currentBooks) {
            file = new File(book.path);
            if (!file.exists())
                deleteBooks.add(book);
        }

        if (deleteBooks.size() > 0) {
            SQLiteDatabase db = new BookDBOpenHelper(mContext).getWritableDatabase();
            db.beginTransaction();

            try {
                String[] whereArgs;
                for (BookData deleteBook : deleteBooks) {
                    whereArgs = new String[]{deleteBook.path};
                    db.delete(RaiderDBContract.ShelfReader.TABLE_NAME
                            , RaiderDBContract.ShelfReader.COLUMN_NAME_PATH + "=?"
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
    public boolean deleteSelectedBooksFromDB(ArrayList<BookData> deleteBooks, boolean deleteFiles) {
        boolean hasException = false;
        SQLiteDatabase db = new BookDBOpenHelper(mContext).getWritableDatabase();
        db.beginTransaction();

        try {
            String[] whereArgs;
            for (BookData deleteBook : deleteBooks) {
                whereArgs = new String[]{deleteBook.path};
                db.delete(RaiderDBContract.ShelfReader.TABLE_NAME
                        , RaiderDBContract.ShelfReader.COLUMN_NAME_PATH + "=?"
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
            for (BookData deleteBook : deleteBooks) {
                file = new File(deleteBook.path);
                if (!file.delete()) {
                    return false;
                }
            }
        }
        return true;
    }

}
