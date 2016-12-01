package com.raider.book.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.raider.book.R;
import com.raider.book.activity.ReadActivity;
import com.raider.book.base.BaseFragment;
import com.raider.book.base.BaseView;
import com.raider.book.custom.sbv.SlideBookView;
import com.raider.book.dao.BookData;
import com.raider.book.mvp.presenter.ReadPresenter;

/**
 * Read book in this fragment.
 */
public class ReadFragment extends BaseFragment implements BaseView<ReadPresenter> {

    ReadPresenter mPresenter;

    SlideBookView bookView;
    private BookData mBookData;

    public static ReadFragment newInstance() {
        return new ReadFragment();
    }

    @Override
    public void _setPresenter(ReadPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_read, container, false);
        FrameLayout fl = (FrameLayout) root.findViewById(R.id.frame_layout);
        initBookView();
        fl.addView(bookView);
        return root;
    }

    private void initBookView() {
        // Receive book data from activity.
        Bundle bundle = getArguments();
        mBookData = bundle.getParcelable(ReadActivity.BUNDLE_BOOK_DATA);
        if (mBookData == null) return;

        bookView = new SlideBookView(mActivity, mBookData, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bookView.setLayoutParams(lp);
    }

}
