package com.raider.book.mvp.presenter;

import android.support.v7.widget.RecyclerView;

import com.raider.book.base.RecyclerPresenter;
import com.raider.book.adapter.JournalAdapter;
import com.raider.book.dao.HttpResult;
import com.raider.book.dao.Journal;
import com.raider.book.mvp.contract.OnlineContract;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class JournalPresenter implements RecyclerPresenter {

    JournalAdapter mAdapter;
    OnlineContract.JournalView iView;
    OnlineContract.JournalModel iModel;
    private Subscription journalSub;

    public JournalPresenter(OnlineContract.JournalView view, OnlineContract.JournalModel model) {
        iView = view;
        iModel = model;
        iView._setPresenter(this);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = (JournalAdapter) adapter;
    }

    @Override
    public void onViewCreated() {
        iView._setAdapter2Presenter();
    }

    @SuppressWarnings("unchecked")
    public void getJournals() {
        journalSub = iModel.journals()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HttpResult<Journal>>() {
                    @Override
                    public void call(HttpResult<Journal> result) {
                        iView._setAdapter2Presenter();
                        mAdapter.addItems(result.data);
                    }
                });
    }

    @Override
    public void onDestroy() {
        iView = null;
        iModel = null;
        if (journalSub != null && !journalSub.isUnsubscribed()) journalSub.unsubscribe();
    }
}
