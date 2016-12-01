package com.raider.book.mvp.contract;

import android.util.SparseIntArray;

import com.raider.book.activity.SDImportActivity;
import com.raider.book.base.RecyclerUI;
import com.raider.book.dao.BookData;
import com.raider.book.mvp.presenter.SDImportPresenter;

import java.util.ArrayList;

public class SDImportContract {

    public interface SmartView extends RecyclerUI<SDImportPresenter> {
        SDImportActivity _getActivity();

        void _handleAddBookSuccess(ArrayList<BookData> addedBooks);
    }

    public interface ScannerModel {
        ArrayList<BookData> getAllFiles();

        ArrayList<BookData> save2DB(SparseIntArray sparseIntArray);
    }

}
