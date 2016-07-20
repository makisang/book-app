package com.raider.book.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raider.book.R;
import com.raider.book.entity.Journal;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.MyViewHolder> {
    Context mContext;
    List<Journal> journals;

    public JournalAdapter(Context context, @NonNull List<Journal> list) {
        this.mContext = context;
        this.journals = list;
    }

    public void addItems(List<Journal> addedList) {
        this.journals.addAll(0, addedList);
//        notifyItemRangeInserted(0, addedList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return journals == null ? 0 : journals.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflateView = LayoutInflater.from(mContext).inflate(R.layout.item_journal, parent, false);
        return new MyViewHolder(inflateView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(journals.get(position).title);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }

    }
}
