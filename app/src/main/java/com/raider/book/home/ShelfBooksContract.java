package com.raider.book.home;

import com.raider.book.RecyclerUI;
import com.raider.book.entity.BookData;

import java.util.ArrayList;
import java.util.List;

public class ShelfBooksContract {

    public interface View extends RecyclerUI<ShelfBooksPresenter> {

        void _showDeleteDialog();

        void _toReadActivity(BookData book);

        void _changeMode(boolean enterSelectMode);

        void _snackDeleteFailureInfo();

        void _disableFab();

        void _enableFab();

        void _showFab();
    }

    public interface Model {
        ArrayList<BookData> loadFromDB();

        ArrayList<BookData> deleteNonexistentFromDB(List<BookData> currentBooks);

        boolean deleteSelectedBooksFromDB(ArrayList<BookData> deleteBooks, boolean deleteFiles);
    }

}
