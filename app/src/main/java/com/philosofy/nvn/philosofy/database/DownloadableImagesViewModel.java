package com.philosofy.nvn.philosofy.database;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

public class DownloadableImagesViewModel extends AndroidViewModel {

    private LiveData<List<DownloadableImage>> downloadableImagesLiveData;
    private AppDatabase db;

    public DownloadableImagesViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(getApplication());
        downloadableImagesLiveData = db.downloadableImagesDao().getAllDownloadableImagesLiveData();
    }

    public LiveData<List<DownloadableImage>> getAllDownloadableImagesLiveData() {
        return downloadableImagesLiveData;
    }

    public LiveData<List<DownloadableImage>> getDownloadableImagesByCategory(String category) {
        return db.downloadableImagesDao().getDownloadableImagesByCategoryLiveData(category);
    }
}
