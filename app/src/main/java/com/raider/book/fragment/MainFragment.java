package com.raider.book.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raider.book.activity.MainActivity;
import com.raider.book.base.BaseFragment;
import com.raider.book.R;
import com.raider.book.adapter.BookInShelfAdapter;
import com.raider.book.mvp.contract.MainContract;
import com.raider.book.dao.BookData;
import com.raider.book.activity.SDImportActivity;
import com.raider.book.activity.ReadActivity;
import com.raider.book.activity.SectionActivity;
import com.raider.book.mvp.presenter.MainPresenter;
import com.raider.book.utils.CustomViewUtils;
import com.raider.book.widget.OffsetDecoration;

import java.util.ArrayList;

/**
 * Main UI for shelf books.
 */
public class MainFragment extends BaseFragment implements MainContract.View {
    private static final int SPAN_COUNT = 3;

    MainPresenter mPresenter;
    private RecyclerView recyclerView;
    private ContentLoadingProgressBar progressBar;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_show_shelf_books, container, false);
        progressBar = (ContentLoadingProgressBar) root.findViewById(R.id.my_progress);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, SPAN_COUNT));
        recyclerView.addItemDecoration(new OffsetDecoration(getResources().getDimensionPixelSize(R.dimen.spacing_mid)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        BookInShelfAdapter adapter = new BookInShelfAdapter(mActivity, new ArrayList<BookData>());
        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.onViewCreated();
        // let presenter init data
        mPresenter.loadBooks();
    }

    public void _setPresenter(MainPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void _setAdapter2Presenter() {
        mPresenter.setAdapter(recyclerView.getAdapter());
    }

    @Override
    public void _scrollToPosition(int position) {
        recyclerView.getLayoutManager().scrollToPosition(position);
    }

    @Override
    public void _toReadActivity(BookData book) {
        ReadActivity.start(mActivity, book);
    }

    @Override
    public void _toSectionActivity() {
        SectionActivity.start(mActivity);
    }

    @Override
    public void _toImportActivity(ArrayList<BookData> books) {
        SDImportActivity.start(mActivity, MainActivity.REQUEST_BOOK_IMPORT, books);
    }

    @Override
    public void _showDeleteDialog() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.dialog_layout_cb, recyclerView, false);
        final AppCompatCheckBox checkBox = (AppCompatCheckBox) contentView.findViewById(R.id.dialog_cb);

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setCancelable(true)
                .setView(contentView)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.removeBooksFromShelf(checkBox.isChecked());
                    }
                })
                .create()
                .show();
    }

    @Override
    public MainActivity _getActivity() {
        return (MainActivity) mActivity;
    }

    @Override
    public void _snackDeleteFailureInfo() {
        Snackbar.make(recyclerView, R.string.delete_failure, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void _hideProgress() {
        CustomViewUtils.hideProgress(mActivity.getApplicationContext(), recyclerView, progressBar);
    }

    @Override
    public void _showProgress() {
        CustomViewUtils.showProgress(mActivity.getApplicationContext(), recyclerView, progressBar);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

}
