package com.raider.book.ui.view;

import com.raider.book.model.entity.BookData;

import java.util.ArrayList;

public interface IBookImportView {
    /**
     *
     * @param books Model层从SD卡扫描出的BookData
     */
    void showBooks(ArrayList<BookData> books);

    void hideProgress();

    void showProgress();

    /**
     * 导书成功，显示提示信息
     */
    void showSuccessHint(ArrayList<BookData> addedBooks);
}
