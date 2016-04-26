package com.raider.book.model;

import com.raider.book.model.entity.BookData;

import java.util.ArrayList;

public interface IShelfModel {
    ArrayList<BookData> loadBooksInDB();
}
