package com.philosofy.nvn.philosofy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.philosofy.nvn.philosofy.database.AppDatabase;
import com.philosofy.nvn.philosofy.database.CrudExecutors;
import com.philosofy.nvn.philosofy.utils.Constants;
import com.philosofy.nvn.philosofy.utils.PreferencesUtils;
import com.philosofy.nvn.philosofy.utils.StorageUtils;
import com.philosofy.nvn.philosofy.utils.SyncUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    //TODO Put real ad ids.
    private static final String TAG = MainActivity.class.getSimpleName();
    private static Uri CAMERA_PHOTO_URI = null;

    private Toolbar mMainToolbar;
    private AdView mBannerAdView;

    private CardView mEditorCardView;
    private CardView mCameraCardView;
    private CardView mDownloadCardView;
    private CardView mPhotosCardView;
    private CardView mColorsCardView;
    private CardView mQuotesCardView;

    private ImageView mSettingsImageView;
    private ImageView mShareImageView;
    private ImageView mHelpImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getString(R.string.app_id_admob));

        initiateView();

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mBannerAdView.loadAd(adRequest);

        setSupportActionBar(mMainToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle("");
            actionBar.setSubtitle("");
        }

        clearDownloadableImagesDataIfNeeded();

        mCameraCardView.setOnClickListener(this);
        mColorsCardView.setOnClickListener(this);
        mPhotosCardView.setOnClickListener(this);
        mDownloadCardView.setOnClickListener(this);
        mQuotesCardView.setOnClickListener(this);
        mEditorCardView.setOnClickListener(this);

        mSettingsImageView.setOnClickListener(this);
        mShareImageView.setOnClickListener(this);
        mHelpImageView.setOnClickListener(this);

        SyncUtils.scheduleFetchQodJob(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.action_help:
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void initiateView() {
        mMainToolbar = findViewById(R.id.main_toolbar);
        mBannerAdView = findViewById(R.id.banner_adview_main);

        mEditorCardView = findViewById(R.id.editor_cardview);
        mCameraCardView = findViewById(R.id.camera_cardview);
        mDownloadCardView = findViewById(R.id.download_cardview);
        mPhotosCardView = findViewById(R.id.photos_cardview);
        mColorsCardView = findViewById(R.id.color_cardview);
        mQuotesCardView = findViewById(R.id.quotes_cardview);


        mSettingsImageView = findViewById(R.id.settings_imageview);
        mShareImageView = findViewById(R.id.share_app_imageview);
        mHelpImageView = findViewById(R.id.help_imageview);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.editor_cardview:
                Intent editorIntent = new Intent(this, EditorActivity.class);
                startActivity(editorIntent);
                break;

            case R.id.camera_cardview:
                dispatchClickPhotoIntent();
                break;

            case R.id.color_cardview:
                ColorPickerDialogFragment colorPickerDialogFragment = new ColorPickerDialogFragment();
                colorPickerDialogFragment.setOnColorChosenListener(new ColorPickerDialogFragment.OnColorChosenListener() {
                    @Override
                    public void onColorChosen(int color) {
                        Intent startEditorIntent = new Intent(MainActivity.this, EditorActivity.class);
                        startEditorIntent.putExtra(Constants.EXTRA_COLOR_CODE, color);
                        startActivity(startEditorIntent);
                    }
                });
                colorPickerDialogFragment.show(getSupportFragmentManager(), "COLOR_CHOOSER");
                break;

            case R.id.photos_cardview:
                Intent photosIntent = new Intent();
                photosIntent.setType("image/*");
                photosIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(photosIntent, Constants.REQUEST_CODE_PHOTOS);
                break;

            case R.id.download_cardview:
                Intent downloadsIntent = new Intent(this, DownloadableImagesActivity.class);
                startActivity(downloadsIntent);
                break;

            case R.id.quotes_cardview:
                Intent quotesIntent = new Intent(this, QuotesActivity.class);
                startActivity(quotesIntent);
                break;

            case R.id.settings_imageview:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            case R.id.share_app_imageview:
                shareThisApp();
                break;

            case R.id.help_imageview:
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            Intent startEditorIntent = new Intent(this, EditorActivity.class);

            switch (requestCode) {
                case Constants.REQUEST_CODE_CAMERA:
                    if (CAMERA_PHOTO_URI != null) {
                        startEditorIntent.putExtra(Constants.EXTRA_CAMERA_BUNDLE, CAMERA_PHOTO_URI);
                        startActivity(startEditorIntent);
                    }
                    break;

                case Constants.REQUEST_CODE_PHOTOS:
                    if (data != null) {
                        Uri photoUri = data.getData();
                        startEditorIntent.putExtra(Constants.EXTRA_PHOTOS_URI, photoUri.toString());
                        startActivity(startEditorIntent);
                    }
                    break;
            }
        }
    }

    private void dispatchClickPhotoIntent() {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            File tempPhotoFile = StorageUtils.getANewTemporaryImageFile(this, "camera_pic");
            if (tempPhotoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider", tempPhotoFile);
                CAMERA_PHOTO_URI = photoUri;
                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePicIntent, Constants.REQUEST_CODE_CAMERA);
            }
        }
    }

    private void shareThisApp() {
        Intent shareAppIntent = new Intent(Intent.ACTION_SEND);
        shareAppIntent.setType("text/plain");
        shareAppIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        String shareMessage = "Check out this awesome app!" +
                "\n\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n";
        shareAppIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareAppIntent, getString(R.string.share_app_title)));
    }

    private void clearDownloadableImagesDataIfNeeded() {
        long timeSinceLastUpdate = PreferencesUtils.getElapsedTimeSinceLastImagesUpdate(this);
        long HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1);

        // Delete data if it's older than 23 hours.
        if (timeSinceLastUpdate <= DateUtils.DAY_IN_MILLIS - HOUR_IN_MILLIS) {
            return;
        }

        CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(MainActivity.this).downloadableImagesDao().deleteAllDownloadableImages();
            }
        });
    }
}
