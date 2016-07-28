package com.raider.book.importing.sd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raider.book.BaseFragment;
import com.raider.book.R;
import com.raider.book.adapter.SmartAdapter;
import com.raider.book.entity.BookData;
import com.raider.book.widget.OffsetDecoration;

import java.util.ArrayList;

public class SmartFragment extends BaseFragment implements SDImportContract.SmartView {
    private static final String BOOKS_IN_SHELF = "shelf_books";

    private SmartPresenter mPresenter;
    private SmartAdapter mAdapter;
    private RecyclerView recyclerView;
    private ArrayList<BookData> shelfBooks;

    public static SmartFragment newInstance(ArrayList<BookData> shelfBooks) {
        SmartFragment smartFragment = new SmartFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BOOKS_IN_SHELF, shelfBooks);
        smartFragment.setArguments(bundle);
        return smartFragment;
    }

    @Override
    public void _setPresenter(SmartPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void _setAdapter2Presenter() {
        mPresenter.setAdapter(recyclerView.getAdapter());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("test", "onCreate");
        shelfBooks = getArguments().getParcelableArrayList(BOOKS_IN_SHELF);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("test", "onCreateView");
        View root = inflater.inflate(R.layout.fragment_smart, container, false);
        // init RecyclerView
        mAdapter = new SmartAdapter(mActivity, new ArrayList<BookData>(), shelfBooks);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addItemDecoration(new OffsetDecoration(getResources().getDimensionPixelSize(R.dimen.spacing_mid)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.onViewCreated();
        mPresenter.getSDBooks();
    }

    @Override
    public void _handleAddBookSuccess(ArrayList<BookData> addedBooks) {

    }

    @Override
    public void _scrollToPosition(int position) {

    }

    @Override
    public void _hideProgress() {

    }

    @Override
    public void _showProgress() {

    }

}
