package com.raider.book.mvp.contract;

import com.raider.book.base.RecyclerUI;
import com.raider.book.mvp.presenter.RecommendPresenter;

import rx.Observable;

public class OnlineContract {

    public interface RecommendView extends RecyclerUI<RecommendPresenter> {

    }

    public interface RecommendModel {
        Observable journals();
    }

}
