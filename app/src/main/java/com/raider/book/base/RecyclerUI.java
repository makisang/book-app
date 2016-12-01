package com.raider.book.base;

/**
 * Base View Interface for UI with a RecyclerView.
 *
 * @param <T> Presenter
 */
public interface RecyclerUI<T extends BasePresenter> extends BaseView<T> {

    void _setAdapter2Presenter();

    void _scrollToPosition(int position);

    void _hideProgress();

    void _showProgress();

}
