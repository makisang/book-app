package com.raider.book.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raider.book.R;
import com.raider.book.adapter.SectionAdapter;
import com.raider.book.base.BaseFragment;
import com.raider.book.widget.OffsetDecoration;

import java.util.ArrayList;
import java.util.List;

public class SectionFragment extends BaseFragment {

    private RecyclerView recyclerView;

    public static SectionFragment newInstance() {
        return new SectionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_section, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity.getApplicationContext()));
        recyclerView.addItemDecoration(new OffsetDecoration(getResources().getDimensionPixelSize(R.dimen.spacing_mid)));
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add("第" + i);
        }
        SectionAdapter sectionAdapter = new SectionAdapter(mActivity, list);
        recyclerView.setAdapter(sectionAdapter);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
