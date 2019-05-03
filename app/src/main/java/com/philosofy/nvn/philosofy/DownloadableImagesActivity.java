package com.philosofy.nvn.philosofy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.philosofy.nvn.philosofy.adapters.QuoteImagesAdapter;
import com.philosofy.nvn.philosofy.adapters.DownloadableImagesAdapter;
import com.philosofy.nvn.philosofy.database.AppDatabase;
import com.philosofy.nvn.philosofy.database.CrudExecutors;
import com.philosofy.nvn.philosofy.database.DownloadableImage;
import com.philosofy.nvn.philosofy.database.DownloadableImagesViewModel;
import com.philosofy.nvn.philosofy.utils.Constants;
import com.philosofy.nvn.philosofy.utils.NetworkUtils;
import com.philosofy.nvn.philosofy.utils.PreferencesUtils;
import com.philosofy.nvn.philosofy.utils.StorageUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DownloadableImagesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DownloadableImagesAdapter.OnDownloadableImageClickListener,
        LargeImagePreviewDialogFragment.LargeImagePreviewCallback {

    private static final String TAG = DownloadableImagesActivity.class.getSimpleName();

    // Swap a whole new data (not Add) if category was changed.
    private static boolean IS_CATEGORY_CHANGED = true;

    // Is there already a images fetching AsyncTask in action.
    private static boolean IS_ALREADY_BUSY = false;

    // Current images category in view.
    private static String CURRENT_CATEGORY = Constants.CATEGORY_NATURE;

    // Last page of images that was cached for currently selected category.
    private static int CURRENT_CATEGORY_LAST_PAGE = 0;

    private LinearLayout mNoInternetLayout;
    private TextView mImageFilterToolBarTitleTextView;
    private TextView mReloadDataTextView;

    private ProgressBar mLoadMoreProgressBar;
    private DrawerLayout mCategoryDrawer;
    private NavigationView mCategoriesNavigationView;

    private RecyclerView mDownloadBackgroundsRecyclerView;

    private DownloadableImagesAdapter mDownloadableImagesAdapter;

    private Toolbar mImageFilterToolbar;

    private AppDatabase mDb;

    private QuoteImagePreviewDialogFragment.QuoteImageCallbacks quoteImageCallbacks
            = new QuoteImagePreviewDialogFragment.QuoteImageCallbacks() {

        @Override
        public void onQuoteImageEdit(File file) {
            Intent editorIntent = new Intent(DownloadableImagesActivity.this, EditorActivity.class);

            String path = file.getAbsolutePath();
            editorIntent.putExtra(Constants.EXTRA_QUOTE_IMAGE, path);

            String action = getIntent().getAction();
            if (action != null && action.equals(Constants.ACTION_BACKGROUND_REQUEST)) {
                setResult(RESULT_OK, editorIntent);
                finish();
            } else {
                startActivity(editorIntent);
            }
        }

        @Override
        public void onQuoteImageDelete(File file) {
            showDeleteConfirmationDialog(file);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadable_images);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initiateViews();

        mDb = AppDatabase.getInstance(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mCategoryDrawer, toolbar, R.string.category_nav_drawer_open, R.string.category_nav_drawer_close);
        mCategoryDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mCategoriesNavigationView.setNavigationItemSelectedListener(this);

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mDownloadBackgroundsRecyclerView.setLayoutManager(layoutManager);
        mDownloadableImagesAdapter = new DownloadableImagesAdapter(this, this, null);
        mDownloadBackgroundsRecyclerView.setAdapter(mDownloadableImagesAdapter);

        mDownloadBackgroundsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)
                        && !CURRENT_CATEGORY.equals(Constants.DOWNLOADED) && !IS_ALREADY_BUSY) {
                    Log.i(TAG, "Last page for category:" + CURRENT_CATEGORY + " is " + CURRENT_CATEGORY_LAST_PAGE);
                    fetchFreshDownloadableImages(1 + CURRENT_CATEGORY_LAST_PAGE);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });

        mImageFilterToolbar.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = getString(R.string.pixabay_url);
                Intent pixabayIntent = new Intent(Intent.ACTION_VIEW);
                pixabayIntent.setData(Uri.parse(urlString));
                startActivity(pixabayIntent);
            }
        });

        mReloadDataTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillUpImagesDataByCategory(CURRENT_CATEGORY);
                mNoInternetLayout.setVisibility(View.INVISIBLE);
            }
        });

        mImageFilterToolBarTitleTextView.setText(CURRENT_CATEGORY.toUpperCase());
        saveLastImagesUpdateTimeIfNeeded();
        setupImagesDataObserver();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_downloadable_images_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        IS_CATEGORY_CHANGED = true;
        mDownloadBackgroundsRecyclerView.setVisibility(View.INVISIBLE);
        switch (id) {
            case R.id.downloaded:
                CURRENT_CATEGORY = Constants.DOWNLOADED;
                mImageFilterToolBarTitleTextView.setText(CURRENT_CATEGORY.toUpperCase());
                mCategoryDrawer.closeDrawer(GravityCompat.START);
                showDownloadedImages();
                return true;

            case R.id.nature:
                CURRENT_CATEGORY = Constants.CATEGORY_NATURE;
                break;

            case R.id.backgrounds:
                CURRENT_CATEGORY = Constants.CATEGORY_BACKGROUNDS;
                break;

            case R.id.people:
                CURRENT_CATEGORY = Constants.CATEGORY_PEOPLE;
                break;

            case R.id.feelings:
                CURRENT_CATEGORY = Constants.CATEGORY_FEELINGS;
                break;

            case R.id.fashion:
                CURRENT_CATEGORY = Constants.CATEGORY_FASHION;
                break;

            case R.id.travel:
                CURRENT_CATEGORY = Constants.CATEGORY_TRAVEL;
                break;

            case R.id.places:
                CURRENT_CATEGORY = Constants.CATEGORY_PLACES;
                break;

            case R.id.food:
                CURRENT_CATEGORY = Constants.CATEGORY_FOOD;
                break;

            case R.id.health:
                CURRENT_CATEGORY = Constants.CATEGORY_HEALTH;
                break;

            case R.id.science:
                CURRENT_CATEGORY = Constants.CATEGORY_SCIENCE;
                break;

            case R.id.education:
                CURRENT_CATEGORY = Constants.CATEGORY_EDUCATION;
                break;

            case R.id.computer:
                CURRENT_CATEGORY = Constants.CATEGORY_COMPUTER;
                break;

            case R.id.music:
                CURRENT_CATEGORY = Constants.CATEGORY_MUSIC;
                break;

            case R.id.sports:
                CURRENT_CATEGORY = Constants.CATEGORY_SPORTS;
                break;

            case R.id.business:
                CURRENT_CATEGORY = Constants.CATEGORY_BUSINESS;
                break;

            case R.id.animals:
                CURRENT_CATEGORY = Constants.CATEGORY_ANIMALS;
                break;

            case R.id.buildings:
                CURRENT_CATEGORY = Constants.CATEGORY_BUILDINGS;
                break;

            case R.id.industry:
                CURRENT_CATEGORY = Constants.CATEGORY_INDUSTRY;
                break;
        }

        mImageFilterToolBarTitleTextView.setText(CURRENT_CATEGORY.toUpperCase());
        fillUpImagesDataByCategory(CURRENT_CATEGORY);
        CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                CURRENT_CATEGORY_LAST_PAGE =
                        mDb.downloadableImagesDao().getLastPageForCategory(CURRENT_CATEGORY);
            }
        });

        mCategoryDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDownloadedImages();
                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                    StorageUtils.showExplanationForSettingsDialog(this);
                } else {
                    Toast.makeText(this, "Action not performed because permission denied.", Toast.LENGTH_SHORT).show();
                }
        }

    }

    @Override
    public void onDownloadableImageClick(String largeImageUrl) {
        LargeImagePreviewDialogFragment largeImagePreviewDialogFragment = new LargeImagePreviewDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_LARGE_IMAGE_URL, largeImageUrl);
        largeImagePreviewDialogFragment.setArguments(bundle);
        largeImagePreviewDialogFragment.setLargeImagePreviewCallback(this);
        largeImagePreviewDialogFragment.show(getSupportFragmentManager(), "PICK_BACKGROUND");
    }

    @Override
    public void onSaveDownloadableImage(Bitmap bitmap) {
        if (!StorageUtils.isStoragePermissionGranted(this)) {
            StorageUtils.tryRequestStoragePermissionForSaving(this);
        } else {
            File file = StorageUtils.getANewImageFile(Constants.IMAGE_DOWNLOADED);
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Toast.makeText(this, "SAVED!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEditDownloadableImage(String url) {

        Intent quoteIntent = new Intent(this, EditorActivity.class);
        quoteIntent.putExtra(Constants.EXTRA_QUOTE_IMAGE, url);

        String action = getIntent().getAction();
        if (action != null && action.equals(Constants.ACTION_BACKGROUND_REQUEST)) {
            // If image was requested through the editor.
            setResult(RESULT_OK, quoteIntent);
            finish();
        } else {
            // If image was selected directly from downloadable images.
            startActivity(quoteIntent);
        }
    }

    private class DownloadBackgroundsTask extends AsyncTask<URL, Void, List<DownloadableImage>> {
        @Override
        protected void onPreExecute() {
            if (!NetworkUtils.hasInternetAccess(DownloadableImagesActivity.this)) {
                mDownloadBackgroundsRecyclerView.setVisibility(View.INVISIBLE);
                mNoInternetLayout.setVisibility(View.VISIBLE);
                mReloadDataTextView.setVisibility(View.VISIBLE);
                cancel(true);
                return;
            }

            mLoadMoreProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<DownloadableImage> doInBackground(URL... urls) {
            URL url = urls[0];
            Log.i(TAG, "Formed Url: " + url);

            String jsonResponse;
            List<DownloadableImage> imagesData = null;

            try {
                IS_ALREADY_BUSY = true;
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                imagesData = NetworkUtils.getImagesDataFromJson(jsonResponse, CURRENT_CATEGORY,
                        "all", CURRENT_CATEGORY_LAST_PAGE + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return imagesData;
        }

        @Override
        protected void onPostExecute(final List<DownloadableImage> imagesData) {
            IS_ALREADY_BUSY = false;
            mLoadMoreProgressBar.setVisibility(View.INVISIBLE);
            CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.downloadableImagesDao().insertDownloadableImages(imagesData);
                    CURRENT_CATEGORY_LAST_PAGE = CURRENT_CATEGORY_LAST_PAGE + 1;
                }
            });

            mDownloadBackgroundsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void fetchFreshDownloadableImages(int pageNo) {
        new DownloadBackgroundsTask().execute(NetworkUtils.getUrlFromImageCategory(this, CURRENT_CATEGORY, pageNo));
    }

    private void fillUpImagesDataByCategory(final String category) {
        if (mDownloadBackgroundsRecyclerView.getAdapter().getClass().getSimpleName()
                .equals(QuoteImagesAdapter.class.getSimpleName())) {
            mDownloadBackgroundsRecyclerView.setAdapter(mDownloadableImagesAdapter);
            mDownloadBackgroundsRecyclerView.setVisibility(View.VISIBLE);
        }

        Log.i(TAG, "fillUpImagesDataByCategory " + category);

        CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<DownloadableImage> downloadableImages
                        = mDb.downloadableImagesDao().getDownloadableImagesByCategory(category);
                if (downloadableImages == null || downloadableImages.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fetchFreshDownloadableImages(1);
                            //After which observer is triggered.
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadableImagesAdapter.swapData(downloadableImages);
                            mDownloadBackgroundsRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void setupImagesDataObserver() {
        Log.i(TAG, "setupImagesDataObserver : " + CURRENT_CATEGORY);
        // Don't proceed to make network calls for non-existent downloaded category in API.
        if (CURRENT_CATEGORY.equals(Constants.DOWNLOADED)) {
            showDownloadedImages();
            return;
        }

        DownloadableImagesViewModel imagesViewModel = ViewModelProviders.of(this)
                .get(DownloadableImagesViewModel.class);

        LiveData<List<DownloadableImage>> downloadableImages = imagesViewModel.getDownloadableImagesByCategory(CURRENT_CATEGORY);

        downloadableImages.observe(this, new Observer<List<DownloadableImage>>() {
            @Override
            public void onChanged(@Nullable List<DownloadableImage> downloadableImages) {
                if (downloadableImages == null || downloadableImages.size() == 0) {
                    Log.i(TAG, "imagesData is null or empty fetching fresh data");
                    IS_CATEGORY_CHANGED = true;
                    fetchFreshDownloadableImages(1);
                    return;
                }

                Log.i(TAG, "Found cached data");
                if (IS_CATEGORY_CHANGED) {
                    Log.i(TAG, "Swapping the data");
                    CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            final List<DownloadableImage> currentCategoryImagesData
                                    = mDb.downloadableImagesDao().getDownloadableImagesByCategory(CURRENT_CATEGORY);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDownloadableImagesAdapter.swapData(currentCategoryImagesData);
                                }
                            });
                        }
                    });

                } else {
                    Log.i(TAG, "Adding more data");
                    CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            final List<DownloadableImage> currentCategoryImagesData
                                    = mDb.downloadableImagesDao().getDownloadableImagesByCategory(CURRENT_CATEGORY);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDownloadableImagesAdapter.addImagesData(currentCategoryImagesData);
                                    mDownloadBackgroundsRecyclerView.scrollBy(0, 300);
                                }
                            });
                        }
                    });
                }
                IS_CATEGORY_CHANGED = false;
            }
        });
    }

    private void showDownloadedImages() {
        if (!StorageUtils.isStoragePermissionGranted(this)) {
            StorageUtils.tryRequestStoragePermissionForSaving(this);
            return;
        }

        if (mNoInternetLayout.getVisibility() != View.INVISIBLE ||
                mReloadDataTextView.getVisibility() != View.INVISIBLE) {
            mNoInternetLayout.setVisibility(View.INVISIBLE);
            mReloadDataTextView.setVisibility(View.INVISIBLE);
        }

        File downloadedDirectory = StorageUtils.getStoragePath(Constants.IMAGE_DOWNLOADED);
        File[] files = downloadedDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.getPath().endsWith(".png"));
            }
        });

        if (files != null && files.length > 0) {
            QuoteImagesAdapter quoteImagesAdapter = new QuoteImagesAdapter(this, new QuoteImagesAdapter.OnQuoteImageClickedListener() {
                @Override
                public void onQuoteImageClicked(File file) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.KEY_BUNDLE_DESIGNED_QUOTE, file);
                    QuoteImagePreviewDialogFragment quoteImagePreviewDialogFragment
                            = new QuoteImagePreviewDialogFragment();
                    quoteImagePreviewDialogFragment.setArguments(bundle);
                    quoteImagePreviewDialogFragment.setOnQuoteImageDeleteListener(quoteImageCallbacks);
                    quoteImagePreviewDialogFragment.show(getSupportFragmentManager(), "DOWNLOADED_IMAGE");
                }
            }, files);
            mDownloadBackgroundsRecyclerView.setAdapter(quoteImagesAdapter);
            mDownloadBackgroundsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initiateViews() {
        mLoadMoreProgressBar = findViewById(R.id.load_more_images_progress_bar);
        mCategoryDrawer = findViewById(R.id.drawer_layout);
        mCategoriesNavigationView = findViewById(R.id.nav_view_downloadable_images);
        mImageFilterToolBarTitleTextView = findViewById(R.id.image_filter_toolbar_title);
        mDownloadBackgroundsRecyclerView = findViewById(R.id.downloadable_images_rv);
        mNoInternetLayout = findViewById(R.id.no_internet_backgrounds_textview);
        mReloadDataTextView = findViewById(R.id.reload_textview);
        mImageFilterToolbar = findViewById(R.id.image_filter_toolbar);
    }

    private void clearAllData() {
        CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.downloadableImagesDao().deleteAllDownloadableImages();
            }
        });
    }

    private void saveLastImagesUpdateTimeIfNeeded() {
        CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<DownloadableImage> imagesData = mDb.downloadableImagesDao().getAllDownloadableImagesData();
                if (imagesData.isEmpty()) {
                    PreferencesUtils.saveLastImageUpdateTime(DownloadableImagesActivity.this, System.currentTimeMillis());
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_error)
                .setTitle("Confirm Delete?")
                .setMessage("Are you sure you want to delete it?");

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File toBeDeletedFile = new File(file.getAbsolutePath());
                boolean isDeleted = toBeDeletedFile.delete();

                if (isDeleted) {
                    //Refresh data after delete
                    showDownloadedImages();
                    Toast.makeText(DownloadableImagesActivity.this, "DELETED!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(DownloadableImagesActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}

