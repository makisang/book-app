package com.raider.book.online;

import com.raider.book.RecyclerUI;

import rx.Observable;

public class OnlineContract {

    interface JournalView extends RecyclerUI<JournalPresenter> {

    }

    interface JournalModel {
        Observable journals();
    }

}
