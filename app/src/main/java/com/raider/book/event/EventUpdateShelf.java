package com.raider.book.event;

import com.raider.book.model.entity.BookData;

import java.util.ArrayList;

public class EventUpdateShelf {
    public ArrayList<BookData> addedBooks;

    public EventUpdateShelf(ArrayList<BookData> addedBooks) {
        this.addedBooks = addedBooks;
    }
}
