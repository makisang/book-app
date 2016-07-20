package com.raider.book.events;

import android.content.Intent;

import com.raider.book.entity.BookData;
import com.raider.book.home.ShelfBooksPresenter;

import java.util.ArrayList;

/**
 * poster: {@link com.raider.book.home.MainActivity#onActivityResult(int, int, Intent)}
 * register: {@link ShelfBooksPresenter#insertInFragment(InsertBooks)}
 */
public class InsertBooks {
    public ArrayList<BookData> addedBooks;

    public InsertBooks(ArrayList<BookData> addedBooks) {
        this.addedBooks = addedBooks;
    }
}
