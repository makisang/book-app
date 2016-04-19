package com.raider.book.engine;

import android.os.Environment;

import com.raider.book.model.entity.BookData;
import com.raider.book.utils.SDCardUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by wkq on 2016/4/12.
 * traverse all .txt files.
 */
public class TraverseBook {
    private static final String TAG = "test";
    private static final String FILE_FILTER_TXT = ".txt";

    private volatile static boolean shutdownRequested = false;
    private static final ArrayList<BookData> books = new ArrayList<>();

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
        return books;
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
                BookData book = new BookData(parseName(file.getName()), file.getAbsolutePath());
                books.add(book);
            }
        }
    }

    private static String parseName(String name) {
//        Log.w(TAG, name);
        return name.substring(0, name.length() - (FILE_FILTER_TXT.length()));
    }

}
