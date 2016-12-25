package com.raider.book.mvp.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.raider.book.base.RecyclerPresenter;
import com.raider.book.adapter.JournalAdapter;
import com.raider.book.dao.HttpResult;
import com.raider.book.dao.NetBook;
import com.raider.book.interf.MyItemClickListener;
import com.raider.book.mvp.contract.OnlineContract;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class JournalPresenter implements RecyclerPresenter {

    JournalAdapter mAdapter;
    OnlineContract.JournalView iView;
    OnlineContract.JournalModel iModel;
    private Subscription journalSub;
    private List<NetBook> netBooks;

    public JournalPresenter(OnlineContract.JournalView view, OnlineContract.JournalModel model) {
        iView = view;
        iModel = model;
        iView._setPresenter(this);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = (JournalAdapter) adapter;
        mAdapter.setItemClick(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NetBook netBook = netBooks.get(position);

            }
        });
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
                        netBooks = result.dataList;
                        mAdapter.addItems(0, netBooks);
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
