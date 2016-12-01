package com.raider.book.mvp.contract;

import com.raider.book.activity.MainActivity;
import com.raider.book.base.RecyclerUI;
import com.raider.book.dao.BookData;
import com.raider.book.mvp.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

public class MainContract {

    public interface View extends RecyclerUI<MainPresenter> {
        MainActivity _getActivity();

        void _snackDeleteFailureInfo();

        void _showDeleteDialog();

        void _toReadActivity(BookData book);

        void _toSectionActivity();

        void _toImportActivity(ArrayList<BookData> books);
    }

    public interface Model {
        ArrayList<BookData> loadFromDB();

        ArrayList<BookData> deleteNonexistentFromDB(List<BookData> currentBooks);

        boolean deleteSelectedBooksFromDB(ArrayList<BookData> deleteBooks, boolean deleteFiles);
    }

}
