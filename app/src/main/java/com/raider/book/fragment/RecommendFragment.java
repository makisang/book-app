package com.raider.book.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.raider.book.R;
import com.raider.book.activity.ReadActivity;
import com.raider.book.adapter.JournalAdapter;
import com.raider.book.contract.Constants;
import com.raider.book.dao.NetBook;
import com.raider.book.interf.MyItemClickListener;
import com.raider.book.mvp.contract.OnlineContract;
import com.raider.book.mvp.presenter.RecommendPresenter;
import com.raider.book.widget.OffsetDecoration;

import java.util.ArrayList;

import rx.functions.Action1;

public class RecommendFragment extends Fragment implements OnlineContract.RecommendView {

    Activity mActivity;
    private JournalAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecommendPresenter mPresenter;

    @Override
    public void _setPresenter(RecommendPresenter presenter) {
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

    public RecommendFragment() {
    }

    public static RecommendFragment newInstance() {
        return new RecommendFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new JournalAdapter(mActivity, new ArrayList<NetBook>());
        mAdapter.setItemClick(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NetBook netBook = mAdapter.findItemInPosition(position);
                ReadActivity.start(getActivity(), netBook);
            }
        });
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
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.onViewCreated();

        mPresenter.getJournals(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.d(Constants.ERROR_TAG, throwable.toString());
                Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

}
