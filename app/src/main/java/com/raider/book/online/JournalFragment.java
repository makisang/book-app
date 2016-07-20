package com.raider.book.online;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raider.book.R;
import com.raider.book.adapter.JournalAdapter;
import com.raider.book.entity.HttpResult;
import com.raider.book.entity.Journal;
import com.raider.book.widget.OffsetDecoration;

import java.util.ArrayList;

public class JournalFragment extends Fragment implements OnlineContract.JournalView {
    private JournalAdapter mAdapter;
    private JournalPresenter mPresenter;

    @Override
    public void _setPresenter(JournalPresenter presenter) {
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
    public void _hideProgress() {

    }

    @Override
    public void _showProgress() {

    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            HttpResult<Journal> result = (HttpResult<Journal>) msg.obj;
            mAdapter.addItems(result.data);
        }
    }

    Activity mActivity;
    private RecyclerView recyclerView;

    public JournalFragment() {
    }

    public static JournalFragment newInstance() {
        return new JournalFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new JournalAdapter(mActivity, new ArrayList<Journal>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_journal, container, false);
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

        mPresenter.getJournals();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

}
