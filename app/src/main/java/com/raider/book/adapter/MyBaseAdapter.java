package com.raider.book.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.raider.book.interf.MyItemClickListener;
import com.raider.book.interf.MyItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

public abstract class MyBaseAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    protected Context mContext;
    protected List<T> mDataList;
    protected MyItemClickListener mItemClickListener;
    protected MyItemLongClickListener mItemLongClickListener;

    public MyBaseAdapter(Context context, @NonNull List<T> list) {
        mContext = context;
        mDataList = list;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setItemClick(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setItemLongClick(MyItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public int findItemIndex(T data) {
        return mDataList.indexOf(data);
    }

    public T findItemInPosition(int position) {
        return mDataList.get(position);
    }

    public void addItem(T data) {
        mDataList.add(data);
        notifyItemInserted(0);
    }

    public void addItem(T data, int position) {
        mDataList.add(position, data);
        notifyItemInserted(position);
    }

    public void addItems(int position, List<T> dataList) {
        if (mDataList.addAll(position, dataList)) {
            notifyItemRangeInserted(position, dataList.size());
        }
    }

    public int deleteItem(T data) {
        int position = mDataList.indexOf(data);
        if (position >= 0) {
            mDataList.remove(position);
            notifyItemRemoved(position);
            return position;
        }
        return -1;
    }

}
