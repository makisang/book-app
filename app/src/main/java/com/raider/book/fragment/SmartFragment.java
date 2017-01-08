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
import com.raider.book.dao.LocalBook;
import com.raider.book.activity.MainActivity;
import com.raider.book.interf.MyCheckChangedListener;
import com.raider.book.interf.MyItemClickListener;
import com.raider.book.mvp.contract.SDImportContract;
import com.raider.book.mvp.presenter.SDImportPresenter;
import com.raider.book.utils.CustomViewUtils;
import com.raider.book.widget.OffsetDecoration;

import java.util.ArrayList;

public class SmartFragment extends BaseFragment implements SDImportContract.SmartView, MyCheckChangedListener, MyItemClickListener {
    private static final String BOOKS_IN_SHELF = "shelf_books";

    private SDImportPresenter mPresenter;
    private RecyclerView recyclerView;
    private ArrayList<LocalBook> shelfBooks;
    private ContentLoadingProgressBar progressBar;
    private SmartAdapter mAdapter;

    public static SmartFragment newInstance(ArrayList<LocalBook> shelfBooks) {
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
        progressBar = (ContentLoadingProgressBar) root.findViewById(R.id.my_progress);
        initRecyclerView(root);
        mPresenter.getSDBooks();
        return root;
    }

    private void initRecyclerView(View root) {
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new OffsetDecoration(getResources().getDimensionPixelSize(R.dimen.spacing_mid)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new SmartAdapter(mActivity, new ArrayList<LocalBook>(), shelfBooks);

        mAdapter.setItemClick(this);
        mAdapter.setCheckChangedListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void checkedSizeChanged(int oldSize, int newSize) {
        if (oldSize == newSize) return;
        if (newSize == 0) {
            ((SDImportActivity) mActivity).hideFab();
        } else {
            ((SDImportActivity) mActivity).updateFabNumber(newSize);
            if (oldSize == 0) {
                ((SDImportActivity) mActivity).showFab();
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    /**
     * Called from fab click in {@link SDImportActivity}
     */
    public void addBooks() {
        mPresenter.addToShelf(mAdapter.getCheckedBooks());
    }

    @Override
    public SDImportActivity _getActivity() {
        return (SDImportActivity) mActivity;
    }

    @Override
    public void _showBooks(ArrayList<LocalBook> books) {
        mAdapter.addItems(0, books);
        CustomViewUtils.hideProgress(mActivity.getApplicationContext(), recyclerView, progressBar);
    }

    @Override
    public void _handleAddBookSuccess(ArrayList<LocalBook> addedBooks) {
        MainActivity.back(mActivity, addedBooks);
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
