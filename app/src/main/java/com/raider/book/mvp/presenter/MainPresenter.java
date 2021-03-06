package com.raider.book.mvp.presenter;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.raider.book.activity.MainActivity;
import com.raider.book.base.RecyclerPresenter;
import com.raider.book.adapter.BookInShelfAdapter;
import com.raider.book.mvp.contract.MainContract;
import com.raider.book.interf.MyItemClickListener;
import com.raider.book.interf.MyItemLongClickListener;
import com.raider.book.dao.LocalBook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainPresenter implements RecyclerPresenter, MyItemClickListener, MyItemLongClickListener {
    MainActivity mActivity;
    BookInShelfAdapter mAdapter;
    MainContract.View iView;
    MainContract.Model iModel;
    private Subscription subscription;

    public MainPresenter(MainContract.View view, MainContract.Model model) {
        iView = view;
        iModel = model;
        iView._setPresenter(this);
    }

    /**
     * Set adapter into presenter.
     */
    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = (BookInShelfAdapter) adapter;
        mAdapter.setItemClick(this);
        mAdapter.setItemLongClick(this);
    }

    public BookInShelfAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Show books in shelf.
     */
    public void loadBooks() {
        iView._showProgress();
        subscription = Observable.just(true)
                .map(new Func1<Boolean, ArrayList<LocalBook>>() {
                    @Override
                    public ArrayList<LocalBook> call(Boolean aBoolean) {
                        return iModel.loadFromDB();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<LocalBook>>() {
                    @Override
                    public void call(ArrayList<LocalBook> books) {
                        if (mAdapter == null) {
                            iView._setAdapter2Presenter();
                        }
                        addBooks(books);
                        iView._hideProgress();
                        mActivity.showFab();
                    }
                });


//        Flowable.just(true)
//                .map(new Function<Boolean, ArrayList<LocalBook>>() {
//                    @Override
//                    public ArrayList<LocalBook> apply(Boolean aBoolean) throws Exception {
//                        return iModel.loadFromDB();
//                    }
//                })
//                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<ArrayList<LocalBook>>() {
//                    @Override
//                    public void accept(ArrayList<LocalBook> books) throws Exception {
//                        if (mAdapter == null) {
//                            iView._setAdapter2Presenter();
//                        }
//                        addBooks(books);
//                        iView._hideProgress();
//                        mActivity.showFab();
//                    }
//                });
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
                        ArrayList<LocalBook> selectedBooks = mAdapter.getSelectedBooks();
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
                            mActivity.changeMode(false);
                        } else {
                            iView._snackDeleteFailureInfo();
                        }
                    }
                });


//        Flowable.just(deleteFile)
//                .map(new Function<Boolean, Boolean>() {
//                    @Override
//                    public Boolean apply(Boolean aBoolean) throws Exception {
//                        ArrayList<LocalBook> selectedBooks = mAdapter.getSelectedBooks();
//                        return iModel.deleteSelectedBooksFromDB(selectedBooks, aBoolean);
//                    }
//                })
//                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean success) throws Exception {
//                        if (success) {
//                            // remove data from adapter and update RecyclerView
//                            mAdapter.removeSelected();
//                            mAdapter.setMode(BookInShelfAdapter.NORMAL_MODE);
//                            mActivity.changeMode(false);
//                        } else {
//                            iView._snackDeleteFailureInfo();
//                        }
//                    }
//                });
    }

    /**
     * Delete non-existent books from db,
     * and notify view refresh.
     */
    private void deleteNonExistentBooks() {
        mActivity.disableFab();
        Observable.just(mAdapter.getDataList())
                .map(new Func1<List<LocalBook>, ArrayList<LocalBook>>() {
                    @Override
                    public ArrayList<LocalBook> call(List<LocalBook> currentBooks) {
                        return iModel.deleteNonexistentFromDB(currentBooks);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<LocalBook>>() {
                    @Override
                    public void call(ArrayList<LocalBook> books) {
                        if (books == null) {
                            iView._snackDeleteFailureInfo();
                        }
                        deleteBooks(books);
                        mActivity.enableFab();
                    }
                });


//        Flowable.just(mAdapter.getDataList())
//                .map(new Function<List<LocalBook>, ArrayList<LocalBook>>() {
//                    @Override
//                    public ArrayList<LocalBook> apply(List<LocalBook> currentBooks) throws Exception {
//                        return iModel.deleteNonexistentFromDB(currentBooks);
//                    }
//                })
//                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<ArrayList<LocalBook>>() {
//                    @Override
//                    public void accept(ArrayList<LocalBook> books) throws Exception {
//                        if (books == null) {
//                            iView._snackDeleteFailureInfo();
//                        }
//                        deleteBooks(books);
//                        mActivity.enableFab();
//                    }
//                });
    }

    private void deleteBooks(ArrayList<LocalBook> deleteBooks) {
        if (deleteBooks == null || deleteBooks.size() == 0)
            return;
        for (LocalBook deleteBook : deleteBooks) {
            mAdapter.deleteItem(deleteBook);
        }
    }

    public void addBooks(ArrayList<LocalBook> books) {
        mAdapter.addItems(0, books);
        iView._scrollToPosition(0);
    }

    public void selectAll() {
        mAdapter.selectAll();
    }

    public void deleteSelected() {
        iView._showDeleteDialog();
    }

    public void exitSelectMode() {
        mAdapter.setMode(BookInShelfAdapter.NORMAL_MODE);
        mAdapter.clearSelect();
    }

    public void toImportActivity() {
        iView._toImportActivity((ArrayList<LocalBook>) mAdapter.getDataList());
    }

    @Override
    public void onViewCreated() {
        iView._setAdapter2Presenter();
        mActivity = iView._getActivity();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (!mAdapter.isInSelectMode()) {
            // TODO: test purpose.
            if (position == 0) {
                iView._toSectionActivity();
                return;
            }
            LocalBook localBook = mAdapter.findItemInPosition(position);
            // confirm this file exists
            File file = new File(localBook.path);
            if (!file.exists()) {
                deleteNonExistentBooks();
                return;
            }
            iView._toReadActivity(localBook);
        } else {
            reactOnPosition(position);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        if (!mAdapter.isInSelectMode()) {
            mAdapter.setMode(BookInShelfAdapter.SELECT_MODE);
            mActivity.changeMode(true);
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
            mActivity.changeMode(false);
        }
    }

    @Override
    public void onDestroy() {
        iView = null;
        iModel = null;
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
    }

}
