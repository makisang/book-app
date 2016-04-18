package com.raider.book.model;

import android.content.Context;
import android.util.SparseIntArray;

public interface IBookImportModel {
    void traverse();

    void stopTraverse();

    void save2DB(Context context, SparseIntArray sparseIntArray);
}
