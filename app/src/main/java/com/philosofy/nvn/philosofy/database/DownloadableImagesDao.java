package com.philosofy.nvn.philosofy.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public abstract class DownloadableImagesDao {

    @Query("SELECT * FROM downloadable_images ORDER BY page_no ASC, views DESC")
    public abstract LiveData<List<DownloadableImage>> getAllDownloadableImagesLiveData();

    @Query("SELECT * FROM downloadable_images ORDER BY page_no ASC, views DESC")
    public abstract List<DownloadableImage> getAllDownloadableImagesData();

    @Query("SELECT * FROM downloadable_images WHERE category=:category ORDER BY page_no ASC, views DESC")
    public abstract LiveData<List<DownloadableImage>> getDownloadableImagesByCategoryLiveData(String category);

    @Query("SELECT * FROM downloadable_images WHERE category=:category ORDER BY page_no ASC, views DESC")
    public abstract List<DownloadableImage> getDownloadableImagesByCategory(String category);

    @Query("DELETE FROM downloadable_images WHERE category=:category")
    public abstract void deleteDownloadableImages(String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertDownloadableImages(List<DownloadableImage> downloadableImagesList);

    @Query("SELECT MAX(page_no) FROM downloadable_images WHERE category=:category")
    public abstract int getLastPageForCategory(String category);

    @Transaction
    public void replaceDownloadableImages(String category, List<DownloadableImage> downloadableImageList) {
        this.deleteDownloadableImages(category);
        insertDownloadableImages(downloadableImageList);
    }

    @Query("DELETE FROM downloadable_images")
    public abstract void deleteAllDownloadableImages();
}
