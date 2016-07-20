package com.raider.book.home;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.raider.book.BaseFragment;
import com.raider.book.R;
import com.raider.book.adapter.BookInShelfAdapter;
import com.raider.book.entity.BookData;
import com.raider.book.importing.sd.SDImportActivity;
import com.raider.book.read.ReadActivity;
import com.raider.book.utils.CustomAnim;
import com.raider.book.widget.OffsetDecoration;

import java.util.ArrayList;

/**
 * Main UI for shelf books.
 */
public class ShelfBooksFragment extends BaseFragment implements ShelfBooksContract.View {
    private static final int SPAN_COUNT = 3;

    ShelfBooksPresenter mPresenter;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private ContentLoadingProgressBar progressBar;

    public static ShelfBooksFragment newInstance() {
        return new ShelfBooksFragment();
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

        fab = (FloatingActionButton) (mActivity).findViewById(R.id.my_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // jump to SDImportActivity
                SDImportActivity.start(mActivity, MainActivity.REQUEST_BOOK_IMPORT, mPresenter.getAdapter().getDataList());
            }
        });
    }

    public void _setPresenter(ShelfBooksPresenter presenter) {
        this.mPresenter = presenter;
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
    public void _changeMode(boolean enterSelectMode) {
        MainActivity activity = (MainActivity) mActivity;
        if (enterSelectMode) {
            hideFab();
            activity.showVisualToolBar();
        } else {
            _showFab();
            activity.hideVisualToolBar();
        }
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
    public void _snackDeleteFailureInfo() {
        Snackbar.make(recyclerView, R.string.delete_failure, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void _hideProgress() {
        CustomAnim.hideProgress(mActivity.getApplicationContext(), recyclerView, progressBar);
    }

    @Override
    public void _showProgress() {
        CustomAnim.showProgress(mActivity.getApplicationContext(), recyclerView, progressBar);
    }

    @Override
    public void _enableFab() {
        fab.setEnabled(true);
    }

    @Override
    public void _disableFab() {
        fab.setEnabled(false);
    }

    @Override
    public void _showFab() {
        fab.show();
        fab.setEnabled(true);
    }

    private void hideFab() {
        fab.setEnabled(false);
        fab.hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

}
