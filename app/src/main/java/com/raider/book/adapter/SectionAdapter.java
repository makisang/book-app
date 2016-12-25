package com.raider.book.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raider.book.R;

import java.util.List;

public class SectionAdapter extends MyBaseAdapter<String, SectionAdapter.MyViewHolder> {

    public SectionAdapter(Context context, @NonNull List<String> list) {
        super(context, list);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_book_section, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.sectionName.setText(mDataList.get(position));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sectionName;

        public MyViewHolder(View itemView) {
            super(itemView);
            sectionName = (TextView) itemView.findViewById(R.id.section_name);
        }
    }

}
