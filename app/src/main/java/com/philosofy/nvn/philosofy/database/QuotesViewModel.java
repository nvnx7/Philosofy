package com.philosofy.nvn.philosofy.database;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.philosofy.nvn.philosofy.utils.Constants;

import java.util.List;

public class QuotesViewModel extends AndroidViewModel {

    private LiveData<List<Quote>> qodsList;
    private LiveData<List<Quote>> userQuotesList;

    public QuotesViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(getApplication());
        qodsList = db.quotesDao().getQuotesLiveDataByType(Constants.QUOTE_DAILY_QODS);
        userQuotesList = db.quotesDao().getQuotesLiveDataByType(Constants.QUOTE_USER);
    }

    public LiveData<List<Quote>> getQodsLiveData() {
        return qodsList;
    }
    public LiveData<List<Quote>> getUserQuotesLiveData() {return userQuotesList;}
}
