package com.raider.book.presenter;

import android.content.Context;
import android.util.SparseIntArray;

import com.raider.book.model.IBookImportModel;
import com.raider.book.model.entity.BookData;
import com.raider.book.model.impl.BookImportModelImpl;
import com.raider.book.ui.view.IBookImportView;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BookImportPresenter {
    private Context mContext;
    private IBookImportView iBookImportView;
    private IBookImportModel iBookImportModel;

    public BookImportPresenter(Context context, IBookImportView iBookImportView) {
        this.mContext = context;
        this.iBookImportView = iBookImportView;
        this.iBookImportModel = new BookImportModelImpl();
    }

    // 扫描所有电子书
    public void startTraverse() {
        iBookImportView.showProgress();
        _getObservable1().subscribe(_getObserver1());
    }

    private rx.Observable<ArrayList<BookData>> _getObservable1() {
        return Observable.just(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Boolean, ArrayList<BookData>>() {
                    @Override
                    public ArrayList<BookData> call(Boolean aBoolean) {
                        return iBookImportModel.traverse();
                    }
                });
    }

    private Action1<ArrayList<BookData>> _getObserver1() {
        return new Action1<ArrayList<BookData>>() {
            @Override
            public void call(ArrayList<BookData> books) {
                iBookImportView.showBooks(books);
                iBookImportView.hideProgress();
            }
        };
    }

    // 将选中的书添加到书架
    public void addToShelf(SparseIntArray sparseIntArray) {
        _getObservable2(sparseIntArray).subscribe(_getObserver2());
    }

    private rx.Observable<ArrayList<BookData>> _getObservable2(SparseIntArray sparseIntArray) {
        return Observable.just(sparseIntArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<SparseIntArray, ArrayList<BookData>>() {
                    @Override
                    public ArrayList<BookData> call(SparseIntArray sparseIntArray) {
                        return iBookImportModel.save2DB(mContext, sparseIntArray);
                    }
                });
    }

    private Action1<ArrayList<BookData>> _getObserver2() {
        return new Action1<ArrayList<BookData>>() {
            @Override
            public void call(ArrayList<BookData> addedBooks) {
                iBookImportView.showSuccessHint(addedBooks);
            }
        };
    }

    public void onDestroy() {
        iBookImportModel.stopTraverse();
        iBookImportModel = null;
    }

}
