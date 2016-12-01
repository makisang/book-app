package com.raider.book.base;

public interface BaseView<T extends BasePresenter> {
    void _setPresenter(T presenter);
}
