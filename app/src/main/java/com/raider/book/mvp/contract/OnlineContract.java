package com.raider.book.mvp.contract;

import com.raider.book.base.RecyclerUI;
import com.raider.book.mvp.presenter.JournalPresenter;

import rx.Observable;

public class OnlineContract {

    public interface JournalView extends RecyclerUI<JournalPresenter> {

    }

    public interface JournalModel {
        Observable journals();
    }

}
