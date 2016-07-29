package com.raider.book.home;

import com.raider.book.RecyclerUI;
import com.raider.book.entity.BookData;

import java.util.ArrayList;
import java.util.List;

public class MainContract {

    public interface View extends RecyclerUI<MainPresenter> {
        MainActivity _getActivity();

        void _snackDeleteFailureInfo();

        void _showDeleteDialog();

        void _toReadActivity(BookData book);

        void _toImportActivity(ArrayList<BookData> books);
    }

    public interface Model {
        ArrayList<BookData> loadFromDB();

        ArrayList<BookData> deleteNonexistentFromDB(List<BookData> currentBooks);

        boolean deleteSelectedBooksFromDB(ArrayList<BookData> deleteBooks, boolean deleteFiles);
    }

}
