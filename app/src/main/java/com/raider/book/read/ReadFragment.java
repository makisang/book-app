package com.raider.book.read;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raider.book.BaseFragment;
import com.raider.book.BaseView;
import com.raider.book.R;
import com.raider.book.custom.sbv.SlideBookView;
import com.raider.book.custom.sbv.SlideModel;
import com.raider.book.custom.sbv.SlidePresenter;
import com.raider.book.entity.BookData;

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
        bookView = (SlideBookView) root.findViewById(R.id.book_view);
        SlidePresenter presenter = new SlidePresenter(bookView, new SlideModel());
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get book data from activity
        Bundle bundle = getArguments();
        mBookData = bundle.getParcelable(ReadActivity.BUNDLE_BOOK_DATA);

        if (mBookData == null) return;

        // set book path to BookView
        bookView.setBook(mBookData.path);
    }
}
