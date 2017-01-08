package com.raider.book.mvp.contract;

import android.util.SparseIntArray;

import com.raider.book.activity.SDImportActivity;
import com.raider.book.base.RecyclerUI;
import com.raider.book.dao.LocalBook;
import com.raider.book.mvp.presenter.SDImportPresenter;

import java.util.ArrayList;

public class SDImportContract {

    public interface SmartView extends RecyclerUI<SDImportPresenter> {
        SDImportActivity _getActivity();

        void _showBooks(ArrayList<LocalBook> books);

        void _handleAddBookSuccess(ArrayList<LocalBook> addedBooks);
    }

    public interface ScannerModel {
        ArrayList<LocalBook> getAllFiles();

        ArrayList<LocalBook> save2DB(SparseIntArray sparseIntArray);
    }

}
