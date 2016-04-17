package com.raider.book.ui.view;

import com.raider.book.model.entity.BookData;

import java.util.ArrayList;

public interface IBookImportView {
    void showBooks(ArrayList<BookData> books);

    void hideProgress();

    void showProgress();
}
