package com.raider.book.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raider.book.R;
import com.raider.book.model.entity.BookData;

import java.util.ArrayList;

/**
 * 在书架中显示.txt文件
 */
public class BookInShelfAdapter extends RecyclerView.Adapter<BookInShelfAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<BookData> shelfBooks;

    public BookInShelfAdapter(Context context, ArrayList<BookData> shelfBooks) {
        this.mContext = context;
        this.shelfBooks = shelfBooks;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_book_in_shelf, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BookData book = shelfBooks.get(position);
        holder.textView.setText(book.name);
    }

    @Override
    public int getItemCount() {
        return shelfBooks == null ? 0 : shelfBooks.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_tv);
        }
    }
}
