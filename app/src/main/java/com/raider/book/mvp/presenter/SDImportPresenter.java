package com.raider.book.mvp.presenter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;

import com.raider.book.activity.MainActivity;
import com.raider.book.base.RecyclerPresenter;
import com.raider.book.mvp.contract.SDImportContract;
import com.raider.book.dao.LocalBook;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SDImportPresenter implements RecyclerPresenter {
    private SDImportContract.SmartView iView;
    private SDImportContract.ScannerModel iModel;
    private Subscription subscription;

    public SDImportPresenter(SDImportContract.SmartView view, SDImportContract.ScannerModel model) {
        iView = view;
        iModel = model;
        iView._setPresenter(this);
    }

    @Override
    public void onViewCreated() {
//        iView._setAdapter2Presenter();
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
    }

    /**
     * Get all .txt files from SD.
     */
    public void getSDBooks() {
        subscription = Observable.just(null)
                .map(new Func1<Object, ArrayList<LocalBook>>() {
                    @Override
                    public ArrayList<LocalBook> call(Object o) {
                        return iModel.getAllFiles();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<LocalBook>>() {
                    @Override
                    public void call(ArrayList<LocalBook> books) {
                        iView._showBooks(books);
                    }
                });
    }

    /**
     * Add selected books to shelf,
     * after done, return to {@link MainActivity}.
     */
    public void addToShelf(SparseIntArray checkedBooks) {
        Observable.just(checkedBooks)
                .map(new Func1<SparseIntArray, ArrayList<LocalBook>>() {
                    @Override
                    public ArrayList<LocalBook> call(SparseIntArray sparseIntArray) {
                        return iModel.save2DB(sparseIntArray);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<LocalBook>>() {
                    @Override
                    public void call(ArrayList<LocalBook> addedBooks) {
                        iView._handleAddBookSuccess(addedBooks);
                    }
                });
    }

    @Override
    public void onDestroy() {
        iView = null;
        iModel = null;
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
    }

}
