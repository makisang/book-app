package com.raider.book.engine;

import android.os.Environment;
import android.util.Log;

import com.raider.book.model.entity.BookData;
import com.raider.book.utils.SDCardUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by wkq on 2016/4/12.
 * traverse all .txt files.
 */
public class TraverseBook {
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
            Log.v("test", "externalStorageDirectory is: " + path);
            File file = new File(path);
            findTXT(file);
        }
        return books;
    }

    private static void findTXT(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            File.listRoots();
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
            if (file.getName().endsWith(".txt")) {
                // 获取BookData对象，放入集合中
                BookData book = new BookData(file.getName(), file.getAbsolutePath());
                books.add(book);
            }
        }
    }

}
