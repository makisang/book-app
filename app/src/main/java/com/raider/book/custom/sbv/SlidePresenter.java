package com.raider.book.custom.sbv;

import com.raider.book.BasePresenter;

import java.nio.MappedByteBuffer;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SlidePresenter implements BasePresenter {
    private SlideContract.View iView;
    private SlideContract.Model iModel;

    @Override
    public void onViewCreated() {

    }

    public SlidePresenter(SlideContract.View view, SlideContract.Model model) {
        iView = view;
        iModel = model;
        iView._setPresenter(this);
    }

    public void loadBook(String path) {
        Observable.just(path)
                .map(new Func1<String, MappedByteBuffer>() {
                    @Override
                    public MappedByteBuffer call(String path) {
                        return iModel.getMBB(path);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MappedByteBuffer>() {
                    @Override
                    public void call(MappedByteBuffer mappedByteBuffer) {
                        iView.setMBB(mappedByteBuffer);
                    }
                });
    }

    @Override
    public void onDestroy() {
        iView = null;
        iModel = null;
    }
}
