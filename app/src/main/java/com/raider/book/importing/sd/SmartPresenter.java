package com.raider.book.importing.sd;

import android.support.v7.widget.RecyclerView;

import com.raider.book.RecyclerPresenter;
import com.raider.book.adapter.SmartAdapter;
import com.raider.book.entity.BookData;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SmartPresenter implements RecyclerPresenter {
    SmartAdapter mAdapter;
    SDImportContract.SmartView iView;
    SDImportContract.SmartModel iModel;

    public SmartPresenter(SDImportContract.SmartView view, SDImportContract.SmartModel model) {
        iView = view;
        iModel = model;
        iView._setPresenter(this);
    }

    @Override
    public void onViewCreated() {
        iView._setAdapter2Presenter();
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = (SmartAdapter) adapter;
    }

    public void getSDBooks() {
        Observable.just(null)
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
                    }
                });
    }

    @Override
    public void onDestroy() {
        iView = null;
        iModel = null;
        mAdapter = null;
    }
}
