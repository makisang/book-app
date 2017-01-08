package com.raider.book.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.raider.book.R;
import com.raider.book.base.BaseFragment;
import com.raider.book.base.BaseView;
import com.raider.book.custom.sbv.SlideBookView;
import com.raider.book.dao.Book;
import com.raider.book.interf.BookLoadListener;
import com.raider.book.mvp.presenter.ReadPresenter;
import com.raider.book.utils.CustomViewUtils;

import static com.raider.book.activity.ReadActivity.EXTRA_BOOK;

/**
 * Read book in this fragment.
 */
public class ReadFragment extends BaseFragment implements BaseView<ReadPresenter> {
    ReadPresenter mPresenter;
    SlideBookView bookView;
    private Book mBook;
    private ContentLoadingProgressBar progressBar;

    public static ReadFragment newInstance(Book book) {
        ReadFragment readFragment = new ReadFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_BOOK, book);
        readFragment.setArguments(bundle);
        return readFragment;
    }

    @Override
    public void _setPresenter(ReadPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_read, container, false);
        RelativeLayout rl = (RelativeLayout) root.findViewById(R.id.outer_layout);
        progressBar = (ContentLoadingProgressBar) root.findViewById(R.id.my_progress);
        initBookView();
        rl.addView(bookView);
        return root;
    }

    private void initBookView() {
        Bundle bundle = getArguments();
        mBook = bundle.getParcelable(EXTRA_BOOK);
        if (mBook == null) return;

        bookView = new SlideBookView(mActivity, mBook, null);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bookView.setLayoutParams(lp);
        bookView.addLoadListener(new BookLoadListener() {
            @Override
            public void onLoadCompleted(Boolean success) {
                CustomViewUtils.hideProgress(getActivity().getApplicationContext(), bookView, progressBar);
            }
        });
    }

}
