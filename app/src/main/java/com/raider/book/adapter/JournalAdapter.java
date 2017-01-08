package com.raider.book.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.raider.book.R;
import com.raider.book.dao.NetBook;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import static com.raider.book.contract.URLCollection.QING_STOR_IMG_DIC;

public class JournalAdapter extends MyBaseAdapter<NetBook, RecyclerView.ViewHolder> {

    public JournalAdapter(Context context, @NonNull List<NetBook> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflateView = LayoutInflater.from(mContext).inflate(R.layout.item_net_book, parent, false);
        return new MyViewHolder(inflateView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.title.setText(mDataList.get(position).title);
        viewHolder.author.setText(mDataList.get(position).author);
        viewHolder.description.setText(mDataList.get(position).description);
        try {
            String decode = URLDecoder.decode(QING_STOR_IMG_DIC + mDataList.get(position).title + ".jpg", "utf-8");
            ImageLoader.getInstance().displayImage(decode, viewHolder.cover);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(viewHolder.cardView, viewHolder.getAdapterPosition());
            }
        });
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        TextView author;
        TextView description;
        ImageView cover;

        private MyViewHolder(final View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            description = (TextView) itemView.findViewById(R.id.description);
            cover = (ImageView) itemView.findViewById(R.id.cover);
        }

    }
}
