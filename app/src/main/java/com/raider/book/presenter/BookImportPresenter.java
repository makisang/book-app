package com.raider.book.presenter;

import android.content.Context;
import android.util.SparseIntArray;

import com.raider.book.event.TraverseBookResult;
import com.raider.book.model.IBookImportModel;
import com.raider.book.model.impl.BookImportModelImpl;
import com.raider.book.ui.view.IBookImportView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BookImportPresenter {
    private Context mContext;
    private IBookImportView iBookImportView;
    private IBookImportModel iBookImportModel;

    public BookImportPresenter(Context context, IBookImportView iBookImportView) {
        this.mContext = context;
        this.iBookImportView = iBookImportView;
        this.iBookImportModel = new BookImportModelImpl();
        EventBus.getDefault().register(this);
    }

    public void startTraverse() {
        iBookImportView.showProgress();
        iBookImportModel.traverse();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showBookList(TraverseBookResult result) {
        iBookImportView.showBooks(result.books);
        iBookImportView.hideProgress();
    }

    // 将选中的书添加到书架
    public void addToShelf(SparseIntArray sparseIntArray) {
        iBookImportModel.save2DB(mContext, sparseIntArray);
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        iBookImportModel.stopTraverse();
        iBookImportModel = null;
    }

}
