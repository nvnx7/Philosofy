package com.philosofy.nvn.philosofy.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteFontDao {

    @Query("SELECT * FROM favorite_fonts ORDER BY added_at DESC")
    LiveData<List<FavoriteFont>> getFavoriteFontsLiveData();

    @Query("SELECT font_name FROM favorite_fonts WHERE lang = :lang ORDER BY added_at DESC")
    List<String> getFavoriteFontNamesByLang(String lang);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addNewFavoriteFont(FavoriteFont favoriteFont);

    @Delete
    void removeFontFromFavorite(FavoriteFont favoriteFont);

    @Query("DELETE FROM favorite_fonts WHERE font_name = :fontName AND lang = :lang")
    void removeFavoriteFontByNameAndLang(String fontName, String lang);
}
