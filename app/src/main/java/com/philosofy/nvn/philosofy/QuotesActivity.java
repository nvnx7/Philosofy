package com.philosofy.nvn.philosofy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.philosofy.nvn.philosofy.adapters.QuoteImagesAdapter;
import com.philosofy.nvn.philosofy.adapters.QuotesAdapter;
import com.philosofy.nvn.philosofy.adapters.QuotesPagerAdapter;
import com.philosofy.nvn.philosofy.database.AppDatabase;
import com.philosofy.nvn.philosofy.database.CrudExecutors;
import com.philosofy.nvn.philosofy.database.Quote;
import com.philosofy.nvn.philosofy.utils.Constants;
import com.philosofy.nvn.philosofy.utils.StorageUtils;

import java.io.File;

public class QuotesActivity extends AppCompatActivity
        implements QuotesAdapter.QuotesCallback,
        AddQuoteDialogFragment.OnAddQuoteListener,
        QuoteImagesAdapter.OnQuoteImageClickedListener,
        QuoteImagePreviewDialogFragment.QuoteImageCallbacks {

    private static final String TAG = QuotesActivity.class.getSimpleName();

    private boolean isRequestedFromEditor = false;
    private AdView mQuotesBannerAdView;

    private Toolbar mQuotesToolbar;
    private TabLayout mQuotesTabLayout;
    private ViewPager mQuotesViewPager;
    private Toolbar mTheySaidSoAttributionLayout;
    private FloatingActionButton mAddQuoteFab;

    private AppDatabase mDb;
    private QuoteImagesCallback mQuoteImagesCallback;

    private QuoteImagesFragment quoteImagesFragment;

    public interface QuoteImagesCallback {
        void onQuoteImagesTabSelected(Context context);

        void onStoragePermissionGranted();
    }

    private TabLayout.OnTabSelectedListener mOnQuotesTabSelectedListener
            = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int position = tab.getPosition();
            if (position == 0) {
                showAttribution(true);
                mAddQuoteFab.hide();
            } else if (position == 1) {
                showAttribution(false);
                mAddQuoteFab.setOnClickListener(mOnAddQuoteFabClickListener);
                mAddQuoteFab.show();
            } else {
                mQuoteImagesCallback
                        .onQuoteImagesTabSelected(QuotesActivity.this);
                showAttribution(false);
                mAddQuoteFab.setOnClickListener(mOnDesignQuoteFabClickListener);
                mAddQuoteFab.show();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private View.OnClickListener mOnAddQuoteFabClickListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AddQuoteDialogFragment addQuoteDialogFragment = new AddQuoteDialogFragment();
            addQuoteDialogFragment.show(getSupportFragmentManager(), "ADD_QUOTE");
            addQuoteDialogFragment.setOnAddQuoteListener(QuotesActivity.this);
        }
    };

    private View.OnClickListener mOnDesignQuoteFabClickListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent editorIntent = new Intent(QuotesActivity.this, EditorActivity.class);
            startActivity(editorIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        initiateViews();
        setSupportActionBar(mQuotesToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mQuotesBannerAdView.loadAd(adRequest);

        handleIntent();

        mDb = AppDatabase.getInstance(this);

        mAddQuoteFab.hide();

        QuotesPagerAdapter quotesPagerAdapter = new QuotesPagerAdapter(getSupportFragmentManager());
        quotesPagerAdapter.addFragment(new QuotesFragment());
        quotesPagerAdapter.addFragment(new QuotesFragment());
        quoteImagesFragment = new QuoteImagesFragment();
        mQuoteImagesCallback = quoteImagesFragment;
        quotesPagerAdapter.addFragment(quoteImagesFragment);

        mQuotesViewPager.setAdapter(quotesPagerAdapter);
        mQuotesTabLayout.setupWithViewPager(mQuotesViewPager);

        mQuotesTabLayout.addOnTabSelectedListener(mOnQuotesTabSelectedListener);

        mAddQuoteFab.setOnClickListener(mOnAddQuoteFabClickListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mQuoteImagesCallback.onStoragePermissionGranted();
                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                    StorageUtils.showExplanationForSettingsDialog(this);
                } else {
                    Toast.makeText(this, "Could not show images because permission denied.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_quotes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void initiateViews() {
        mQuotesBannerAdView = findViewById(R.id.banner_adview_quotes);
        mQuotesToolbar = findViewById(R.id.quotes_toolbar);
        mQuotesTabLayout = findViewById(R.id.quotes_tablayout);
        mQuotesViewPager = findViewById(R.id.quotes_viewpager);
        mTheySaidSoAttributionLayout = findViewById(R.id.theysaidso_attribution);
        mAddQuoteFab = findViewById(R.id.add_quote_fab);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action) && action.equals(Constants.ACTION_QUOTE_REQUEST)) {
            isRequestedFromEditor = true;
        }
    }

    @Override
    public void onQuoteImageClicked(File file) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_BUNDLE_DESIGNED_QUOTE, file);
        QuoteImagePreviewDialogFragment quoteImagePreviewDialogFragment
                = new QuoteImagePreviewDialogFragment();
        quoteImagePreviewDialogFragment.setArguments(bundle);
        quoteImagePreviewDialogFragment.setOnQuoteImageDeleteListener(this);
        quoteImagePreviewDialogFragment.show(getSupportFragmentManager(), "DESIGNED_QUOTE");
    }

    @Override
    public void onQuoteImageDelete(File file) {
        showDeleteConfirmationDialog(file);
    }

    @Override
    public void onQuoteImageEdit(File file) {
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
                    //Refresh data fragment after deleted
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.detach(quoteImagesFragment);
                    fragmentTransaction.attach(quoteImagesFragment);
                    fragmentTransaction.commit();
                    Toast.makeText(QuotesActivity.this, "DELETED!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(QuotesActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onEditQuote(String quote) {
        Intent editIntent = new Intent(this, EditorActivity.class);
        if (isRequestedFromEditor) {
            editIntent.putExtra(Constants.PICKED_QUOTE, quote);
            setResult(RESULT_OK, editIntent);
            finish();
        } else {
            editIntent.putExtra(Intent.EXTRA_TEXT, quote);
            startActivity(editIntent);
        }
    }

    @Override
    public void onShareQuote(String quote) {
        String mimeType = "text/plain";
        String title = "Share this Quote";

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, quote);
        shareIntent.setType(mimeType);

        startActivity(Intent.createChooser(shareIntent, title));
    }

    @Override
    public void onCopyQuote(String quote) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            ClipData clipData = ClipData.newPlainText("Quote", quote);
            clipboardManager.setPrimaryClip(clipData);
            Snackbar.make(mQuotesBannerAdView, "Quote Copied.", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSearchAuthor(String author) {
        Intent searchIntent = new Intent();
        searchIntent.setAction(Intent.ACTION_VIEW);
        String authorName = author.trim().replace(" ", "_");
        String uriString = getString(R.string.wiki_base_uri) + authorName;
        Uri uri = Uri.parse(uriString);
        searchIntent.setData(uri);

        if (isAppInstalled(getString(R.string.wiki_package))) {
            searchIntent.setPackage(getString(R.string.wiki_package));
        }

        if (searchIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(searchIntent);
        } else {
            Toast.makeText(this, "No App Available for this feature.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteQuote(final Quote quote) {
        final AlertDialog deleteQuoteAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Quote")
                .setMessage("Do you really want to delete this quote?")
                .setIcon(R.drawable.ic_error)
                .create();

        deleteQuoteAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        //AppDatabase db = AppDatabase.getInstance(QuotesActivity.this);
                        mDb.quotesDao().removeQuote(quote);

                        Snackbar.make(mQuotesToolbar, "Quote deleted.", Snackbar.LENGTH_SHORT);
                    }
                });
            }
        });

        deleteQuoteAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteQuoteAlertDialog.dismiss();
            }
        });

        deleteQuoteAlertDialog.show();
    }

    @Override
    public void onAddQuote(final Quote quote) {
        CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.quotesDao().insertQuote(quote);
            }
        });
    }

    private boolean isAppInstalled(String packageName) {
        try {
            getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void showAttribution(boolean toShow) {
        if (toShow) {

            mTheySaidSoAttributionLayout.animate()
                    .translationY(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mTheySaidSoAttributionLayout.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            mTheySaidSoAttributionLayout.animate()
                    .translationY((mTheySaidSoAttributionLayout.getHeight()))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mTheySaidSoAttributionLayout.setVisibility(View.GONE);
                        }
                    });
        }
    }

}
