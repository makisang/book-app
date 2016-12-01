package com.raider.book.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raider.book.R;
import com.raider.book.dao.BookData;

import java.util.ArrayList;
import java.util.List;

/**
 * 在书架中显示.txt文件
 */
public class BookInShelfAdapter extends MyBaseAdapter<BookData, BookInShelfAdapter.MyViewHolder> {
    public static final int NORMAL_MODE = 0;
    public static final int SELECT_MODE = 1;
    int mode = NORMAL_MODE;

    SparseIntArray selectedIndex;

    public BookInShelfAdapter(Context context, @NonNull List<BookData> shelfBooks) {
        super(context, shelfBooks);
        this.selectedIndex = new SparseIntArray();
    }

    /**
     * Select or deselect item.
     *
     * @param position item position
     */
    public void changeSelectedItem(int position) {
        if (selectedIndex.indexOfKey(position) < 0) {
            selectedIndex.put(position, position);
        } else {
            selectedIndex.delete(position);
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isZeroSelected() {
        return selectedIndex.size() == 0;
    }

    public boolean isInSelectMode() {
        return mode == SELECT_MODE;
    }

    public void selectAll() {
        selectedIndex.clear();
        for (int i = 0; i < dataList.size(); i++) {
            selectedIndex.put(i, i);
        }
        notifyDataSetChanged();
    }

    public void clearSelect() {
        for (int i = 0; i < selectedIndex.size(); i++) {
            notifyItemChanged(selectedIndex.valueAt(i));
        }
        selectedIndex.clear();
    }

    public void removeSelected() {
        int position = 0;
        ArrayList<BookData> selectedBooks = new ArrayList<>();
        for (int i = 0; i < selectedIndex.size(); i++) {
            position = selectedIndex.valueAt(i);
            selectedBooks.add(dataList.get(position));
        }

        for (BookData book : selectedBooks) {
            dataList.remove(book);
        }

        if (selectedIndex.size() == 1) {
            notifyItemRemoved(position);
        } else {
            notifyDataSetChanged();
        }
        selectedIndex.clear();
    }

    public ArrayList<BookData> getSelectedBooks() {
        ArrayList<BookData> selectedBooks = new ArrayList<>();
        for (int i = 0; i < selectedIndex.size(); i++) {
            selectedBooks.add(dataList.get(selectedIndex.valueAt(i)));
        }
        return selectedBooks;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_book_in_shelf, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BookData book = dataList.get(holder.getAdapterPosition());
        holder.textView.setText(book.name);
        holder.cardView.setSelected(getMode() == SELECT_MODE && selectedIndex.indexOfKey(holder.getAdapterPosition()) >= 0);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CardView cardView;
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.book_name);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener == null)
                return;
            mItemClickListener.onItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener == null)
                return false;
            mItemLongClickListener.onItemLongClick(v, getAdapterPosition());
            return true;
        }
    }

}
