package com.raider.book.importing.sd;

import android.util.SparseIntArray;

import com.raider.book.BaseView;
import com.raider.book.entity.BookData;

import java.util.ArrayList;

public class SDImportContract {

    public interface View extends BaseView<SDImportPresenter> {
        void _showBooks(ArrayList<BookData> books);

        void _handleAddBookSuccess(ArrayList<BookData> addedBooks);

        void _hideProgress();

        void _showProgress();
    }

    public interface Model {
        ArrayList<BookData> traverse();

        ArrayList<BookData> save2DB(SparseIntArray sparseIntArray);
    }

}
