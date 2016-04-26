package com.raider.book.ui.view;

import com.raider.book.model.entity.BookData;

import java.util.ArrayList;

public interface IShelfView {

    void updateBookGrid(ArrayList<BookData> books);

    void hideProgress();

    void showProgress();
}
