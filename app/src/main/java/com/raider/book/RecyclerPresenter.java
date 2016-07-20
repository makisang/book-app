package com.raider.book;

import android.support.v7.widget.RecyclerView;

public interface RecyclerPresenter extends BasePresenter {
    void setAdapter(RecyclerView.Adapter adapter);
}
