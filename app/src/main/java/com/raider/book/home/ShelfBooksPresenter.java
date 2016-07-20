package com.raider.book.home;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.raider.book.adapter.BookInShelfAdapter;
import com.raider.book.adapter.MyItemClickListener;
import com.raider.book.adapter.MyItemLongClickListener;
import com.raider.book.RecyclerPresenter;
import com.raider.book.entity.BookData;
import com.raider.book.events.DeleteSelected;
import com.raider.book.events.ExitSelectMode;
import com.raider.book.events.InsertBooks;
import com.raider.book.events.SelectAll;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ShelfBooksPresenter implements RecyclerPresenter, MyItemClickListener, MyItemLongClickListener {
    ShelfBooksContract.View iView;
    ShelfBooksContract.Model iModel;
    BookInShelfAdapter mAdapter;

    public ShelfBooksPresenter(ShelfBooksContract.View iView, ShelfBooksContract.Model iModel) {
        this.iView = iView;
        this.iModel = iModel;
        EventBus.getDefault().register(this);
        this.iView._setPresenter(this);
    }

    /**
     * Set adapter into presenter.
     */
    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = (BookInShelfAdapter) adapter;
        this.mAdapter.setItemClick(this);
        this.mAdapter.setItemLongClick(this);
    }

    public BookInShelfAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Show books in shelf when first enter app
     */
    public void loadBooks() {
        iView._showProgress();
        Observable.just(true)
                .map(new Func1<Boolean, ArrayList<BookData>>() {
                    @Override
                    public ArrayList<BookData> call(Boolean aBoolean) {
                        return iModel.loadFromDB();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<BookData>>() {
                    @Override
                    public void call(ArrayList<BookData> books) {
                        if (mAdapter == null) {
                            iView._setAdapter2Presenter();
                        }
                        addBooks(books);
                        iView._hideProgress();
                        iView._showFab();
                    }
                });
    }

    /**
     * Remove books from shelf.
     *
     * @param deleteFile true: delete files in disk
     */
    public void removeBooksFromShelf(boolean deleteFile) {
        Observable.just(deleteFile)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        ArrayList<BookData> selectedBooks = mAdapter.getSelectedBooks();
                        return iModel.deleteSelectedBooksFromDB(selectedBooks, aBoolean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean success) {
                        if (success) {
                            // remove data from adapter and update RecyclerView
                            mAdapter.removeSelected();
                            mAdapter.setMode(BookInShelfAdapter.NORMAL_MODE);
                            iView._changeMode(false);
                        } else {
                            iView._snackDeleteFailureInfo();
                        }
                    }
                });
    }

    /**
     * Delete non-existent books from db,
     * and notify view refresh.
     */
    private void deleteNonExistentBooks() {
        iView._disableFab();
        Observable.just(mAdapter.getDataList())
                .map(new Func1<List<BookData>, ArrayList<BookData>>() {
                    @Override
                    public ArrayList<BookData> call(List<BookData> currentBooks) {
                        return iModel.deleteNonexistentFromDB(currentBooks);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<BookData>>() {
                    @Override
                    public void call(ArrayList<BookData> books) {
                        if (books == null) {
                            iView._snackDeleteFailureInfo();
                        }
                        deleteBooks(books);
                        iView._enableFab();
                    }
                });
    }

    private void deleteBooks(ArrayList<BookData> deleteBooks) {
        if (deleteBooks == null || deleteBooks.size() == 0)
            return;
        for (BookData deleteBook : deleteBooks) {
            mAdapter.deleteItem(deleteBook);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void insertInFragment(InsertBooks insertBooks) {
        addBooks(insertBooks.addedBooks);
    }

    public void addBooks(ArrayList<BookData> books) {
        mAdapter.addItems(books, 0);
        iView._scrollToPosition(0);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectAll(SelectAll selectAll) {
        mAdapter.selectAll();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteSelected(DeleteSelected deleteSelected) {
        iView._showDeleteDialog();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitSelectMode(ExitSelectMode exitSelectMode) {
        mAdapter.setMode(BookInShelfAdapter.NORMAL_MODE);
        mAdapter.clearSelect();
    }

    @Override
    public void onViewCreated() {
        iView._setAdapter2Presenter();
    }

    @Override
    public void onDestroy() {
        iView = null;
        iModel = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (!mAdapter.isInSelectMode()) {
            BookData bookData = mAdapter.findItemInPosition(position);
            // confirm this file exists
            File file = new File(bookData.path);
            if (!file.exists()) {
                deleteNonExistentBooks();
                return;
            }
            iView._toReadActivity(bookData);
        } else {
            reactOnPosition(position);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        if (!mAdapter.isInSelectMode()) {
            mAdapter.setMode(BookInShelfAdapter.SELECT_MODE);
            iView._changeMode(true);
        }
        reactOnPosition(position);
    }

    /**
     * Change selected item in adapter after (long)click in position.
     * If necessary, change UI mode.
     */
    private void reactOnPosition(int position) {
        mAdapter.changeSelectedItem(position);
        mAdapter.notifyItemChanged(position);
        if (mAdapter.isZeroSelected()) {
            mAdapter.setMode(BookInShelfAdapter.NORMAL_MODE);
            iView._changeMode(false);
        }
    }

}
