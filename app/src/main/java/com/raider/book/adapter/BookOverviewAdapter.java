package com.raider.book.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raider.book.R;
import com.raider.book.model.entity.BookData;

import java.util.ArrayList;

/**
 * 在导书界面中显示所有的书籍信息
 */
public class BookOverviewAdapter extends RecyclerView.Adapter<BookOverviewAdapter.MyViewHolder> {
    Context context;
    ArrayList<BookData> books;

    public BookOverviewAdapter(Context context, ArrayList<BookData> books) {
        this.context = context;
        this.books = books;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_overview, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BookData bookData = books.get(position);
        holder.textView.setText(bookData.name);
    }

    @Override
    public int getItemCount() {
        return books == null ? 0 : books.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        AppCompatCheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.my_tv);
            checkBox = (AppCompatCheckBox) view.findViewById(R.id.my_cb);
        }
    }

}
