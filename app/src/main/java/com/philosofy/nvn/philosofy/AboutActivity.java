package com.philosofy.nvn.philosofy;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private Toolbar mAboutToolbar;
    private TextView mAppVersionTextView;
    private TextView mPicassoLicenseTextView;
    private TextView mPhotoEditorLicenseTextView;
    private TextView mSkydovesLicenseTextView;
    private TextView mPhotoFiltersLicenseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initiateViews();

        setSupportActionBar(mAboutToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        String appVersion = String.format(getString(R.string.app_version), BuildConfig.VERSION_NAME);
        mAppVersionTextView.setText(appVersion);

        String picassoHeader = String.format(getString(R.string.about_license_header),
                getString(R.string.library_name_picasso));
        mPicassoLicenseTextView.setText(picassoHeader);

        String photoEditorHeader = String.format(getString(R.string.about_license_header),
                getString(R.string.library_name_photoeditor));
        mPhotoEditorLicenseTextView.setText(photoEditorHeader);

        String skydovesHeader = String.format(getString(R.string.about_license_header),
                getString(R.string.library_name_skydoves));
        mSkydovesLicenseTextView.setText(skydovesHeader);

        String photoFiltersHeader = String.format(getString(R.string.license_photofilters_header),
                getString(R.string.library_name_photofilters));
        mPhotoFiltersLicenseTextView.setText(photoFiltersHeader);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initiateViews() {
        mAboutToolbar = findViewById(R.id.about_toolbar);
        mAppVersionTextView = findViewById(R.id.app_version_textview);
        mPicassoLicenseTextView = findViewById(R.id.picasso_license_textview);
        mPhotoEditorLicenseTextView = findViewById(R.id.photoeditor_license_textview);
        mSkydovesLicenseTextView = findViewById(R.id.skydoves_license_textview);
        mPhotoFiltersLicenseTextView = findViewById(R.id.photofilters_license_textview);
    }
}
