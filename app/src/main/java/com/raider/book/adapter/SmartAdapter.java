package com.raider.book.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raider.book.R;
import com.raider.book.dao.BookData;
import com.raider.book.interf.MyCheckChangedListener;

import java.util.ArrayList;

/**
 * 显示所有的.txt文件
 */
public class SmartAdapter extends MyBaseAdapter<BookData, SmartAdapter.MyViewHolder> {
    ArrayList<BookData> shelfBooks;
    SparseIntArray sparseIntArray = new SparseIntArray();
    String IN_SHELF;
    MyCheckChangedListener ccListener;

    public SmartAdapter(Context context, ArrayList<BookData> books, ArrayList<BookData> shelfBooks) {
        super(context, books);
        this.shelfBooks = shelfBooks;
        IN_SHELF = context.getResources().getString(R.string.already_in_shelf);
    }

    public void setCheckChangedListener(MyCheckChangedListener listener) {
        ccListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_book_overview, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final int itemPosition = holder.getAdapterPosition();
        BookData bookData = dataList.get(itemPosition);
        holder.name.setText(bookData.name);

        if (shelfBooks != null && shelfBooks.contains(bookData)) {
            holder.status.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.GONE);
            holder.status.setText(IN_SHELF);
            return;
        }

        holder.status.setVisibility(View.GONE);
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(sparseIntArray.indexOfKey(itemPosition) >= 0);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldCheckedSize = sparseIntArray.size();
                if (((AppCompatCheckBox) v).isChecked()) {
                    sparseIntArray.put(itemPosition, itemPosition);
                } else {
                    sparseIntArray.delete(itemPosition);
                }
                ccListener.checkedSizeChanged(oldCheckedSize, sparseIntArray.size());
            }
        });
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView status;
        AppCompatCheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.my_tv);
            status = (TextView) view.findViewById(R.id.status_tv);
            checkBox = (AppCompatCheckBox) view.findViewById(R.id.my_cb);
        }
    }

    // 返回选中的文件索引
    public SparseIntArray getCheckedBooks() {
        return sparseIntArray;
    }
}
