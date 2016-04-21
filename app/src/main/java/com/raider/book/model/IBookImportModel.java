package com.raider.book.model;

import android.content.Context;
import android.util.SparseIntArray;

import com.raider.book.model.entity.BookData;

import java.util.ArrayList;

public interface IBookImportModel {
    ArrayList<BookData> traverse();

    void stopTraverse();

    ArrayList<BookData> save2DB(Context context, SparseIntArray sparseIntArray);
}
