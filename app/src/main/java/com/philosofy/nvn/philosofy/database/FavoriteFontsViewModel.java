package com.philosofy.nvn.philosofy.database;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

public class FavoriteFontsViewModel extends AndroidViewModel {

    private LiveData<List<FavoriteFont>> favoriteFonts;

    public FavoriteFontsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(getApplication());
        favoriteFonts = db.favoriteFontDao().getFavoriteFontsLiveData();
    }

    public LiveData<List<FavoriteFont>> getFavoriteFontsLiveData() {
        return favoriteFonts;
    }

}
