package com.raider.book.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raider.book.activity.SDImportActivity;
import com.raider.book.base.BaseFragment;
import com.raider.book.R;
import com.raider.book.adapter.SmartAdapter;
import com.raider.book.dao.BookData;
import com.raider.book.activity.MainActivity;
import com.raider.book.mvp.contract.SDImportContract;
import com.raider.book.mvp.presenter.SDImportPresenter;
import com.raider.book.utils.CustomViewUtils;
import com.raider.book.widget.OffsetDecoration;

import java.util.ArrayList;

public class SmartFragment extends BaseFragment implements SDImportContract.SmartView {
    private static final String BOOKS_IN_SHELF = "shelf_books";

    private SDImportPresenter mPresenter;
    private RecyclerView recyclerView;
    private ArrayList<BookData> shelfBooks;
    private ContentLoadingProgressBar progressBar;

    public static SmartFragment newInstance(ArrayList<BookData> shelfBooks) {
        SmartFragment smartFragment = new SmartFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BOOKS_IN_SHELF, shelfBooks);
        smartFragment.setArguments(bundle);
        return smartFragment;
    }

    @Override
    public void _setPresenter(SDImportPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void _setAdapter2Presenter() {
        mPresenter.setAdapter(recyclerView.getAdapter());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shelfBooks = getArguments().getParcelableArrayList(BOOKS_IN_SHELF);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_smart, container, false);
        // init RecyclerView
        SmartAdapter mAdapter = new SmartAdapter(mActivity, new ArrayList<BookData>(), shelfBooks);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addItemDecoration(new OffsetDecoration(getResources().getDimensionPixelSize(R.dimen.spacing_mid)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        progressBar = (ContentLoadingProgressBar) root.findViewById(R.id.my_progress);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.onViewCreated();
        mPresenter.getSDBooks();
    }

    @Override
    public SDImportActivity _getActivity() {
        return (SDImportActivity) mActivity;
    }

    @Override
    public void _handleAddBookSuccess(ArrayList<BookData> addedBooks) {
        MainActivity.back(mActivity, addedBooks);
    }

    @Override
    public void _scrollToPosition(int position) {

    }

    @Override
    public void _hideProgress() {
        CustomViewUtils.hideProgress(mActivity.getApplicationContext(), recyclerView, progressBar);
    }

    @Override
    public void _showProgress() {
        CustomViewUtils.showProgress(mActivity.getApplicationContext(), recyclerView, progressBar);
    }

}
