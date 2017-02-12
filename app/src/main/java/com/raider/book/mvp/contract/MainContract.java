package com.raider.book.mvp.contract;

import com.raider.book.activity.MainActivity;
import com.raider.book.base.RecyclerUI;
import com.raider.book.dao.LocalBook;
import com.raider.book.mvp.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

public class MainContract {

    public interface View extends RecyclerUI<MainPresenter> {
        MainActivity _getActivity();

        void _snackDeleteFailureInfo();

        void _showDeleteDialog();

        void _toReadActivity(LocalBook book);

        void _toSectionActivity();

        void _toImportActivity(ArrayList<LocalBook> books);
    }

    public interface Model {
        ArrayList<LocalBook> loadFromDB();

        ArrayList<LocalBook> deleteNonexistentFromDB(List<LocalBook> currentBooks);

        boolean deleteSelectedBooksFromDB(ArrayList<LocalBook> deleteBooks, boolean deleteFiles);
    }

}
