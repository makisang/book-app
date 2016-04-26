package com.raider.book.presenter;


import android.content.Context;

import com.raider.book.model.entity.BookData;
import com.raider.book.model.impl.ShelfModelImpl;
import com.raider.book.ui.view.IShelfView;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ShelfPresenter {
    Context mContext;
    IShelfView iShelfView;
    ShelfModelImpl shelfModel;

    public ShelfPresenter(Context context, IShelfView iShelfView) {
        this.mContext = context;
        this.iShelfView = iShelfView;
        this.shelfModel = new ShelfModelImpl(mContext);
    }

    public void loadBooks() {
        iShelfView.showProgress();
        _getObservable1().subscribe(_getSubscriber1());
    }

    private Observable<ArrayList<BookData>> _getObservable1() {
        return Observable.just(true)
                .map(new Func1<Boolean, ArrayList<BookData>>() {
                    @Override
                    public ArrayList<BookData> call(Boolean aBoolean) {
                        return shelfModel.loadBooksInDB();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Action1<ArrayList<BookData>> _getSubscriber1() {
        return new Action1<ArrayList<BookData>>() {
            @Override
            public void call(ArrayList<BookData> books) {
                iShelfView.updateBookGrid(books);
                iShelfView.hideProgress();
            }
        };
    }

}
