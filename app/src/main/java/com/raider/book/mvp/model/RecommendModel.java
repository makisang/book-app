package com.raider.book.mvp.model;

import android.content.Context;

import com.raider.book.contract.URLCollection;
import com.raider.book.dao.HttpResult;
import com.raider.book.dao.NetBook;
import com.raider.book.mvp.contract.OnlineContract;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public class RecommendModel implements OnlineContract.RecommendModel {
    Context mContext;

    public RecommendModel(Context context) {
        this.mContext = context;
    }

    private interface JournalService {
        @GET("booklist")
        Observable<HttpResult<NetBook>> getJournals(
                @Query("page") int page
        );
    }

    public Observable<HttpResult<NetBook>> journals() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLCollection.BASE_URL)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        JournalService journalService = retrofit.create(JournalService.class);
        return journalService.getJournals(1);
    }
}
