package com.raider.book.mvp.model;

import android.content.Context;

import com.raider.book.dao.HttpResult;
import com.raider.book.dao.Journal;
import com.raider.book.mvp.contract.OnlineContract;

import rx.Observable;

public class JournalModel implements OnlineContract.JournalModel {
    Context mContext;

    public JournalModel(Context context) {
        this.mContext = context;
    }

    public interface JournalService {
//        @GET("journals/1")
//        Observable<HttpResult<Journal>> getJournals(@Query("user_id") int user_id,
//                                                    @Query("start") int start,
//                                                    @Query("count") int count);
    }

    public Observable<HttpResult<Journal>> journals() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(URLCollection.BASE_URL)
//                .addConverterFactory(FastJsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();
//
//        JournalService journalService = retrofit.create(JournalService.class);
//        return journalService.getJournals(1, 1, 20);
        return null;
    }
}
