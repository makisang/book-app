package com.raider.book.importing.sd;

import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;

import com.raider.book.RecyclerPresenter;
import com.raider.book.adapter.SmartAdapter;
import com.raider.book.entity.BookData;
import com.raider.book.interf.MyCheckChangedListener;
import com.raider.book.interf.MyItemClickListener;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SDImportPresenter implements RecyclerPresenter, MyItemClickListener, MyCheckChangedListener {
    SDImportActivity mActivity;
    SmartAdapter mAdapter;
    SDImportContract.SmartView iView;
    SDImportContract.SmartModel iModel;
    private Subscription subscription;

    public SDImportPresenter(SDImportContract.SmartView view, SDImportContract.SmartModel model) {
        iView = view;
        iModel = model;
        iView._setPresenter(this);
    }

    @Override
    public void onViewCreated() {
        iView._setAdapter2Presenter();
        mActivity = iView._getActivity();
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = (SmartAdapter) adapter;
        mAdapter.setItemClick(this);
        mAdapter.setCheckChangedListener(this);
    }

    /**
     * Get all .txt files from SD.
     */
    public void getSDBooks() {
        iView._showProgress();
        subscription = Observable.just(null)
                .map(new Func1<Object, ArrayList<BookData>>() {
                    @Override
                    public ArrayList<BookData> call(Object o) {
                        return iModel.traverse();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<BookData>>() {
                    @Override
                    public void call(ArrayList<BookData> books) {
                        mAdapter.addItems(0, books);
                        iView._hideProgress();
                    }
                });
    }

    /**
     * Add selected books to shelf,
     * after done, return to {@link com.raider.book.home.MainActivity}.
     */
    public void addToShelf() {
        Observable.just(mAdapter.getCheckedBooks())
                .map(new Func1<SparseIntArray, ArrayList<BookData>>() {
                    @Override
                    public ArrayList<BookData> call(SparseIntArray sparseIntArray) {
                        return iModel.save2DB(sparseIntArray);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<BookData>>() {
                    @Override
                    public void call(ArrayList<BookData> addedBooks) {
                        iView._handleAddBookSuccess(addedBooks);
                    }
                });
    }

    @Override
    public void onDestroy() {
        iView = null;
        iModel = null;
        mAdapter = null;
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    /**
     * Update FAB number and visibility.
     *
     * @param oldSize checked book number before changed.
     * @param newSize checked book number after changed.
     */
    @Override
    public void checkedSizeChanged(int oldSize, int newSize) {
        if (oldSize == newSize) return;
        if (newSize == 0) {
            mActivity.hideFab();
        } else {
            mActivity.updateFabNumber(newSize);
            if (oldSize == 0) mActivity.showFab();
        }
    }

}
