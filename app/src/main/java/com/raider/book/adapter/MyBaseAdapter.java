package com.raider.book.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class MyBaseAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    protected Context mContext;
    protected List<T> dataList;
    protected MyItemClickListener mItemClickListener;
    protected MyItemLongClickListener mItemLongClickListener;

    public MyBaseAdapter(Context context, @NonNull List<T> list) {
        this.mContext = context;
        this.dataList = list;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setItemClick(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setItemLongClick(MyItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public int findItemIndex(T data) {
        return dataList.indexOf(data);
    }

    public T findItemInPosition(int position) {
        return dataList.get(position);
    }

    public void addItem(T data) {
        dataList.add(data);
        notifyItemInserted(0);
    }

    public void addItem(T data, int position) {
        dataList.add(position, data);
        notifyItemInserted(position);
    }

    public void addItems(int position, ArrayList<T> dataList) {
        if (this.dataList.addAll(position, dataList)) {
            notifyItemRangeInserted(position, dataList.size());
        }
    }

    public int deleteItem(T data) {
        int position = dataList.indexOf(data);
        if (position >= 0) {
            dataList.remove(position);
            notifyItemRemoved(position);
            return position;
        }
        return -1;
    }

}
