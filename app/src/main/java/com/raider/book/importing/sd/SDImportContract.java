package com.raider.book.importing.sd;

import android.util.SparseIntArray;

import com.raider.book.RecyclerUI;
import com.raider.book.entity.BookData;

import java.util.ArrayList;

public class SDImportContract {

    public interface SmartView extends RecyclerUI<SDImportPresenter> {
        SDImportActivity _getActivity();

        void _handleAddBookSuccess(ArrayList<BookData> addedBooks);
    }

    public interface SmartModel {
        ArrayList<BookData> traverse();

        ArrayList<BookData> save2DB(SparseIntArray sparseIntArray);
    }

}
