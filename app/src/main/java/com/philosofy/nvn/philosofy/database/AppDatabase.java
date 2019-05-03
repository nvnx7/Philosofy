package com.philosofy.nvn.philosofy.database;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import androidx.annotation.NonNull;

import com.philosofy.nvn.philosofy.R;
import com.philosofy.nvn.philosofy.utils.Constants;

import java.util.Date;

@Database(entities = {FavoriteFont.class, Quote.class, DownloadableImage.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "philosofy_db";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .addCallback(new RoomDatabase.Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        String[] defaultFonts = context.getResources().getStringArray(R.array.default_font_families);
                                        String quote = context.getResources().getString(R.string.pre_added_quote);
                                        String author = context.getResources().getString(R.string.pre_added_quote_author);
                                        String category = context.getResources().getString(R.string.pre_added_quote_category);

                                        AppDatabase appDb = getInstance(context);
                                        for (String font : defaultFonts) {
                                            FavoriteFont favoriteFont = new FavoriteFont(font, Constants.LANGUAGE_LATIN, new Date());
                                            appDb.favoriteFontDao().addNewFavoriteFont(favoriteFont);
                                        }
                                        appDb.quotesDao().insertQuote(new Quote(quote, author, category, new Date(), Constants.QUOTE_USER));
                                    }
                                });
                            }
                        })
                        .build();
            }
        }

        return sInstance;
    }

    public abstract FavoriteFontDao favoriteFontDao();

    public abstract QuotesDao quotesDao();

    public abstract DownloadableImagesDao downloadableImagesDao();
}
