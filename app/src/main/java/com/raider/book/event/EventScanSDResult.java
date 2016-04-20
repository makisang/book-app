package com.raider.book.event;

import com.raider.book.model.entity.BookData;

import java.util.ArrayList;

/**
 * Model层中遍历.txt文件得到结果，用来通知Presenter层
 */
public class EventScanSDResult {
    public ArrayList<BookData> books;

    public EventScanSDResult(ArrayList<BookData> books) {
        this.books = books;
    }

}
