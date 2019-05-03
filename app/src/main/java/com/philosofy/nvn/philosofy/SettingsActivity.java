package com.philosofy.nvn.philosofy;

import android.content.SharedPreferences;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.philosofy.nvn.philosofy.database.AppDatabase;
import com.philosofy.nvn.philosofy.database.CrudExecutors;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Toolbar mSettingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSettingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mSettingsToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String safesearchKey = getString(R.string.pref_images_safesearch_key);
        String orientationKey = getString(R.string.pref_images_orientation_key);

        // Fetch fresh images if image related preference is changed
        if (key.equals(safesearchKey) || key.equals(orientationKey)) {
            CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase.getInstance(SettingsActivity.this).downloadableImagesDao().deleteAllDownloadableImages();
                }
            });
        }
    }
}
