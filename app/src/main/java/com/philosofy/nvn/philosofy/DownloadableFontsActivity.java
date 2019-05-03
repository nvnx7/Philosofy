package com.philosofy.nvn.philosofy;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.philosofy.nvn.philosofy.adapters.LanguagesFontsPagerAdapter;
import com.philosofy.nvn.philosofy.database.AppDatabase;
import com.philosofy.nvn.philosofy.database.CrudExecutors;
import com.philosofy.nvn.philosofy.database.FavoriteFont;
import com.philosofy.nvn.philosofy.adapters.DownloadableFontsAdapter;
import com.philosofy.nvn.philosofy.utils.Constants;

import java.util.Date;

public class DownloadableFontsActivity extends AppCompatActivity
        implements DownloadableFontsAdapter.DownloadableFontsCallback {

    private static final String TAG = DownloadableFontsActivity.class.getSimpleName();

    private Toolbar mDownloadableFontsToolbar;
    private TabLayout mLanguagesTabLayout;
    private ViewPager mLanguagesViewPager;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadable_fonts);

        initiateViews();
        setSupportActionBar(mDownloadableFontsToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setSupportActionBar(mDownloadableFontsToolbar);

        mLanguagesViewPager.setAdapter(new LanguagesFontsPagerAdapter(getSupportFragmentManager()));
        mLanguagesTabLayout.setupWithViewPager(mLanguagesViewPager);

        mDb = AppDatabase.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_downloadable_fonts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
        } else if (id == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onFontAddedToFavorites(final String fontFamilyName, final String lang, int favoriteTag) {

        if (favoriteTag == Constants.FAVORITE) {
            CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteFontDao().removeFavoriteFontByNameAndLang(fontFamilyName, lang);
                }
            });

            Snackbar.make(mDownloadableFontsToolbar, "Font removed from editor screen.",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            Date addedAt = new Date();
            final FavoriteFont favoriteFont = new FavoriteFont(fontFamilyName, lang, addedAt);

            CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteFontDao().addNewFavoriteFont(favoriteFont);
                }
            });

            Snackbar.make(mDownloadableFontsToolbar, "Font added to editor screen.",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWriteWithFont(String fontFamilyName) {
        // Go to editor activity
        Intent fontIntent = new Intent(this, EditorActivity.class);
        fontIntent.putExtra(Constants.PICKED_DOWNLOADABLE_FONT, fontFamilyName);
        setResult(RESULT_OK, fontIntent);
        finish();
    }

    private void initiateViews() {
        mDownloadableFontsToolbar = findViewById(R.id.downloadable_fonts_toolbar);
        mLanguagesTabLayout = findViewById(R.id.language_tablayout);
        mLanguagesViewPager = findViewById(R.id.languages_viewpager);
    }

}
