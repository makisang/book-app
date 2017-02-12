package com.raider.book.mvp.presenter;

import android.support.v7.widget.RecyclerView;

import com.raider.book.base.RecyclerPresenter;
import com.raider.book.adapter.JournalAdapter;
import com.raider.book.dao.HttpResult;
import com.raider.book.dao.NetBook;
import com.raider.book.mvp.contract.OnlineContract;


import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RecommendPresenter implements RecyclerPresenter {

    private JournalAdapter mAdapter;
    private OnlineContract.RecommendView iView;
    private OnlineContract.RecommendModel iModel;
    private Subscription journalSub;

    public RecommendPresenter(OnlineContract.RecommendView view, OnlineContract.RecommendModel model) {
        iView = view;
        iModel = model;
        iView._setPresenter(this);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = (JournalAdapter) adapter;
    }

    @Override
    public void onViewCreated() {
        iView._setAdapter2Presenter();
    }

    @SuppressWarnings("unchecked")
    public void getJournals(Action1<Throwable> errorHandler) {
        journalSub = iModel.journals()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HttpResult<NetBook>>() {
                    @Override
                    public void call(HttpResult<NetBook> result) {
                        iView._setAdapter2Presenter();
                        mAdapter.addItems(0, result.dataList);
                    }
                }, errorHandler);
    }

    @Override
    public void onDestroy() {
        iView = null;
        iModel = null;
        if (journalSub != null && !journalSub.isUnsubscribed()) journalSub.unsubscribe();
    }
}
