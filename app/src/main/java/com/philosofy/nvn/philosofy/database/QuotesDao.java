package com.philosofy.nvn.philosofy.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuotesDao {

    @Query("SELECT * FROM quotes ORDER BY date DESC")
    LiveData<List<Quote>> getQuotesLiveData();

    @Query("SELECT * FROM quotes WHERE type = :type ORDER BY date DESC")
    LiveData<List<Quote>> getQuotesLiveDataByType(String type);

    @Query("SELECT * FROM quotes WHERE type = :type ORDER BY date DESC")
    List<Quote> getQuotesListByType(String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuote(Quote quote);

    @Delete
    void removeQuote(Quote quote);
}
