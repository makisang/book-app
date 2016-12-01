package com.raider.book.engine;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.raider.book.dao.BookData;
import com.raider.book.utils.SDCardUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by wkq on 2016/4/12.
 * getAllFiles all .txt files.
 */
public class BookScanner {
    private static final String TAG = "BookScanner";
    private static final String FILE_FILTER_TXT = ".txt";

    private volatile static boolean shutdownRequested = false;
    private static ArrayList<BookData> books = new ArrayList<>();

    public static void shutdown() {
        shutdownRequested = true;
    }

    public static ArrayList<BookData> traverseInSD() {
        shutdownRequested = false;
        if (SDCardUtil.isSDCardAvail()) {
            books.clear();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(path);
            findTXT(file);
        }
        ArrayList<BookData> anotherList = new ArrayList<>(books.size());
        anotherList.addAll(books);
        books.clear();
        return anotherList;
    }

    private static void findTXT(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File _file : files) {
                // Model有无请求停止遍历
                if (!shutdownRequested) {
                    findTXT(_file);
                }
            }
        } else {
            if (file.getName().endsWith(FILE_FILTER_TXT)) {
                // 获取BookData对象，放入集合中
                BookData book = new BookData(parseName(file.getName()), file.getAbsolutePath(), file.length());
                books.add(book);
            }
        }
    }

    private static String parseName(String name) {
        return name.substring(0, name.length() - (FILE_FILTER_TXT.length()));
    }

    public static ArrayList<BookData> getFilesFromMediaStore(ContentResolver cr) {
        Uri externalUri = MediaStore.Files.getContentUri("external");
        Cursor cursor = cr.query(externalUri, null,
                MediaStore.Files.FileColumns.MIME_TYPE + "=?",
                new String[]{"text/plain"},
                MediaStore.Files.FileColumns.DATE_MODIFIED + " desc");
        Log.d(TAG, "MediaStore text/plain count: " + cursor.getCount());
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
            long size = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)));
            BookData book = new BookData(title, path, size);
            books.add(book);
        }
        cursor.close();
        ArrayList<BookData> anotherList = new ArrayList<>(books.size());
        anotherList.addAll(books);
        books.clear();
        return anotherList;
    }

}
