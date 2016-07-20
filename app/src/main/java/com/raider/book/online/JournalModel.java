package com.raider.book.online;

import android.content.Context;

import com.raider.book.contract.URLCollection;
import com.raider.book.entity.HttpResult;
import com.raider.book.entity.Journal;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public class JournalModel implements OnlineContract.JournalModel {
    Context mContext;

    public JournalModel(Context context) {
        this.mContext = context;
    }

    public interface JournalService {
        @GET("journals/1")
        Observable<HttpResult<Journal>> getJournals(@Query("user_id") int user_id,
                                                    @Query("start") int start,
                                                    @Query("count") int count);
    }

    public Observable<HttpResult<Journal>> journals() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLCollection.BASE_URL)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        JournalService journalService = retrofit.create(JournalService.class);
        return journalService.getJournals(1, 1, 20);
    }
}
