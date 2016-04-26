package com.raider.book.presenter;

import android.content.Context;
import android.util.SparseIntArray;

import com.raider.book.model.IBookImportModel;
import com.raider.book.model.entity.BookData;
import com.raider.book.model.impl.BookImportModelImpl;
import com.raider.book.ui.view.IBookImportView;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BookImportPresenter {
    private Context mContext;
    private IBookImportView iBookImportView;
    private IBookImportModel iBookImportModel;
    private Subscription subscription1;
    private Subscription subscription2;

    public BookImportPresenter(Context context, IBookImportView iBookImportView) {
        this.mContext = context;
        this.iBookImportView = iBookImportView;
        this.iBookImportModel = new BookImportModelImpl();
    }

    // 扫描所有电子书
    public void startTraverse() {
        iBookImportView.showProgress();
        subscription1 = _getObservable1().subscribe(_getSubscriber1());
    }

    private rx.Observable<ArrayList<BookData>> _getObservable1() {
        return Observable.just(true)
                .map(new Func1<Boolean, ArrayList<BookData>>() {
                    @Override
                    public ArrayList<BookData> call(Boolean aBoolean) {
                        return iBookImportModel.traverse();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Action1<ArrayList<BookData>> _getSubscriber1() {
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
        subscription2 = _getObservable2(sparseIntArray).subscribe(_getSubscriber2());
    }

    private rx.Observable<ArrayList<BookData>> _getObservable2(SparseIntArray sparseIntArray) {
        return Observable.just(sparseIntArray)
                .map(new Func1<SparseIntArray, ArrayList<BookData>>() {
                    @Override
                    public ArrayList<BookData> call(SparseIntArray sparseIntArray) {
                        return iBookImportModel.save2DB(mContext, sparseIntArray);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Action1<ArrayList<BookData>> _getSubscriber2() {
        return new Action1<ArrayList<BookData>>() {
            @Override
            public void call(ArrayList<BookData> addedBooks) {
                iBookImportView.handleSuccess(addedBooks);
            }
        };
    }

    public void onDestroy() {
        iBookImportModel = null;
        if (subscription1 != null) {
            subscription1.unsubscribe();
        }
        if (subscription2 != null) {
            subscription2.unsubscribe();
        }
    }

}
