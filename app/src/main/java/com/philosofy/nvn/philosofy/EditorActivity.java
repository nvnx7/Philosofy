package com.philosofy.nvn.philosofy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.provider.FontsContractCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.philosofy.nvn.philosofy.adapters.FilterThumbnailsAdapter;
import com.philosofy.nvn.philosofy.adapters.FontsAdapter;
import com.philosofy.nvn.philosofy.adapters.StockImagesAdapter;
import com.philosofy.nvn.philosofy.adapters.TextToolPagerAdapter;
import com.philosofy.nvn.philosofy.adapters.ToolsAdapter;
import com.philosofy.nvn.philosofy.custom.NonSwipeableViewPager;
import com.philosofy.nvn.philosofy.custom.ThumbnailItemDecoration;
import com.philosofy.nvn.philosofy.custom.ToolType;
import com.philosofy.nvn.philosofy.custom.ToolsItemDecoration;
import com.philosofy.nvn.philosofy.database.AppDatabase;
import com.philosofy.nvn.philosofy.database.CrudExecutors;
import com.philosofy.nvn.philosofy.database.FavoriteFont;
import com.philosofy.nvn.philosofy.utils.BmpUtils;
import com.philosofy.nvn.philosofy.utils.Constants;
import com.philosofy.nvn.philosofy.utils.FontUtils;
import com.philosofy.nvn.philosofy.utils.PreferencesUtils;
import com.philosofy.nvn.philosofy.utils.StorageUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnViewSelectedListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

public class EditorActivity extends AppCompatActivity implements
        OnPhotoEditorListener,
        ToolsAdapter.OnToolSelectedListener,
        TextDialogFragment.OnDoneTextEditListener,
        FilterThumbnailsAdapter.FilterThumbnailCallback,
        View.OnClickListener,
        StockImagesAdapter.OnStockImageSelectedListener,
        FontsAdapter.FavoriteFontsCallback,
        OnViewSelectedListener,
        FinalizeDialogFragment.OnFinalizeImageCallback {

    private static final String TAG = EditorActivity.class.getSimpleName();

    // Is the current image already saved
    private static boolean IS_SAVED = false;

    // Current rotation of background
    private static int CURRENT_ROTATION_ANGLE = 0;

    // Current photo filter applied
    private static String CURRENT_PHOTO_FILTER = "None";

    private PhotoEditorView mPhotoEditorView;
    private PhotoEditor mPhotoEditor;

    // Recycler Views
    private RecyclerView mToolsRecyclerView;
    private RecyclerView mStockImagesRecyclerView;
    private RecyclerView mFiltersRecyclerView;

    // Image views for control
    private ImageView mAddPhotoImageView;
    private ImageView mUndoImageView;
    private ImageView mRedoImageView;
    private ImageView mDoneEditImageView;
    private ImageView mRotateBackgroundImageView;
    private ImageView mMoreBackgroundsImageView;

    // Progress bar for processing
    private ProgressBar mEditorProgressBar;

    // Bottom Sheets
    private ConstraintLayout mTextBottomSheet;
    private ConstraintLayout mImageBottomSheet;
    private ConstraintLayout mBackgroundBottomSheet;
    private ConstraintLayout mEffectsBottomSheet;

    // Bottom Sheet Behaviors
    private BottomSheetBehavior mTextBottomSheetBehavior;
    private BottomSheetBehavior mImageBottomSheetBehavior;
    private BottomSheetBehavior mBackgroundBottomSheetBehavior;
    private BottomSheetBehavior mEffectBottomSheetBehavior;

    // Dialog fragments
    TextDialogFragment mTextDialogFragment;

    private NonSwipeableViewPager mTextControlsViewPager;

    // Seek Bars
    private SeekBar mBrightnessSeekBar;
    private SeekBar mBlurSeekBar;
    private SeekBar mImageSizeSeekBar;
    private SeekBar mImageOpacitySeekBar;

    private SeekBar.OnSeekBarChangeListener mImageControlsSeekBarChangeListener
            = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            ImageView selectedImage = (ImageView) getSelectedViewByType(ViewType.IMAGE);

            if (selectedImage == null) {
                return;
            }

            switch (seekBar.getId()) {
                case R.id.image_size_seekbar:
                    int scale = progress + Constants.MIN_IMAGE_SIZE;
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(scale, scale);
                    selectedImage.setLayoutParams(params);
                    break;

                case R.id.image_opacity_seekbar:
                    int opacity = progress + Constants.MIN_IMAGE_OPACITY;
                    selectedImage.setImageAlpha(opacity);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private SeekBar.OnSeekBarChangeListener mBackgroundControlsSeekBarChangeListener
            = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (seekBar.getId() == R.id.brightness_seekbar) {
                if (progress >= 100) {
                    int value = (int) Math.round(200.0 * (((float) (progress - 100)) / 100.0));
                    mPhotoEditorView.getSource().setColorFilter(BmpUtils.getBrightColorFilter(value));
                } else {
                    int value = (100 - progress) * 255 / 100;
                    mPhotoEditorView.getSource().setColorFilter(BmpUtils.getDarkColorFilter(value));
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (seekBar.getId() == R.id.blur_seekbar) {
                Bitmap bmp = mPhotoEditorView.getOriginalBitmap();
                applyBlur(bmp, seekBar.getProgress());
            }
        }
    };

    private View.OnClickListener mOtherControlsListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.undo_imageview:
                    mPhotoEditor.undo();
                    break;

                case R.id.redo_imageview:
                    mPhotoEditor.redo();
                    break;

                case R.id.done_edit_imageview:
                    mPhotoEditor.clearHelperBox();
                    FinalizeDialogFragment finalizeDialogFragment = new FinalizeDialogFragment();
                    finalizeDialogFragment.show(getSupportFragmentManager(), "FINAL_IMAGE");
                    finalizeDialogFragment.setFinalizedView(mPhotoEditorView);
                    finalizeDialogFragment.setOnSaveImageCallback(EditorActivity.this);
                    break;
            }
        }
    };

    FontsContractCompat.FontRequestCallback fontRequestCallback
            = new FontsContractCompat.FontRequestCallback() {
        @Override
        public void onTypefaceRetrieved(Typeface typeface) {
            TextView selectedText = (TextView) getSelectedViewByType(ViewType.TEXT);
            if (selectedText != null) {
                selectedText.setTypeface(typeface);
            } else {
                Toast.makeText(EditorActivity.this, "No text selected to apply font!" , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTypefaceRequestFailed(int reason) {
            super.onTypefaceRequestFailed(reason);
            Toast.makeText(EditorActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
        }
    };

    private PrimaryTextToolsFragment mPrimaryTextToolsFragment;
    private SecondaryTextToolsFragment mSecondaryTextToolsFragment;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);
        initiateViews();

        mPhotoEditor = new PhotoEditor.Builder(this, this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();
        mPhotoEditor.setOnPhotoEditorListener(this);

        hookRecyclerViewsToAdapters();
        setupBottomSheetBehaviors();

        mPrimaryTextToolsFragment = new PrimaryTextToolsFragment();
        mSecondaryTextToolsFragment = new SecondaryTextToolsFragment();

        Intent intent = getIntent();
        handleIntent(intent);

        TextToolPagerAdapter textToolPagerAdapter = new TextToolPagerAdapter(getSupportFragmentManager());
        textToolPagerAdapter.addFragment(mPrimaryTextToolsFragment);
        textToolPagerAdapter.addFragment(mSecondaryTextToolsFragment);
        mTextControlsViewPager.setAdapter(textToolPagerAdapter);

        mTextBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mAddPhotoImageView.setOnClickListener(this);
        mUndoImageView.setOnClickListener(mOtherControlsListener);
        mRedoImageView.setOnClickListener(mOtherControlsListener);
        mDoneEditImageView.setOnClickListener(mOtherControlsListener);
        mRotateBackgroundImageView.setOnClickListener(this);
        mMoreBackgroundsImageView.setOnClickListener(this);

        mBrightnessSeekBar.setOnSeekBarChangeListener(mBackgroundControlsSeekBarChangeListener);
        mBlurSeekBar.setOnSeekBarChangeListener(mBackgroundControlsSeekBarChangeListener);
        mImageSizeSeekBar.setOnSeekBarChangeListener(mImageControlsSeekBarChangeListener);
        mImageOpacitySeekBar.setOnSeekBarChangeListener(mImageControlsSeekBarChangeListener);

        enableImageControls(false);
    }

    @Override
    public void onBackPressed() {
        if (!mPhotoEditor.isCacheEmpty() && !IS_SAVED) {
            showConfirmExitDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Granted!", Toast.LENGTH_SHORT).show();
                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                    StorageUtils.showExplanationForSettingsDialog(this);
                } else {
                    Toast.makeText(this, "Action not performed because permission denied.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        mTextControlsViewPager.setCurrentItem(0);
        switch (toolType) {
            case TEXT:
                if (mTextBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    expandBottomSheet(mTextBottomSheetBehavior);
                }
                break;

            case BACKGROUND:
                if (mBackgroundBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    expandBottomSheet(mBackgroundBottomSheetBehavior);
                }
                break;

            case EFFECTS:
                if (mEffectBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    expandBottomSheet(mEffectBottomSheetBehavior);
                }
                break;

            case PHOTO:
                if (mImageBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    expandBottomSheet(mImageBottomSheetBehavior);
                }
                break;

            case QUOTE:
                Intent chooseQuoteIntent = new Intent(this, QuotesActivity.class);
                chooseQuoteIntent.setAction(Constants.ACTION_QUOTE_REQUEST);
                startActivityForResult(chooseQuoteIntent, Constants.REQUEST_CODE_QUOTE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add_image_imageview:
                Intent photosIntent = new Intent();
                photosIntent.setType("image/*");
                photosIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(photosIntent, Constants.REQUEST_CODE_PHOTOS);
                break;

            case R.id.more_controls_imageview:
                mTextControlsViewPager.setCurrentItem(1);
                break;

            case R.id.previous_controls_imageview:
                mTextControlsViewPager.setCurrentItem(0);
                break;

            case R.id.rotate_background_imageview:
                rotateBackground(90);
                break;

            case R.id.more_background_images_imageview:
                Intent moreBackgroundsIntent = new Intent(this, DownloadableImagesActivity.class);
                moreBackgroundsIntent.setAction(Constants.ACTION_BACKGROUND_REQUEST);
                startActivityForResult(moreBackgroundsIntent, Constants.REQUEST_CODE_BACKGROUND);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_PHOTOS:
                    Uri photoUri;
                    if (data != null) {
                        photoUri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                            mPhotoEditor.addImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case Constants.REQUEST_CODE_DOWNLOADABLE_FONT:
                    String fontFamilyName;
                    if (data != null) {
                        fontFamilyName = data.getStringExtra(Constants.PICKED_DOWNLOADABLE_FONT);
                        Toast.makeText(this, fontFamilyName, Toast.LENGTH_SHORT).show();
                        FontUtils.requestFont(this, fontFamilyName, fontRequestCallback);
                    } else {
                        Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.REQUEST_CODE_QUOTE:
                    String quote;
                    if (data != null) {
                        quote = data.getStringExtra(Constants.PICKED_QUOTE);
                        TextDialogFragment textDialogFragment = new TextDialogFragment();
                        textDialogFragment.show(getSupportFragmentManager(), "ADD_TEXT");
                        textDialogFragment.setPreText(quote);
                        textDialogFragment.setOnTextDoneListener(EditorActivity.this);
                    }
                    break;

                case Constants.REQUEST_CODE_BACKGROUND:
                    if (data != null) {
                        String uri = data.getStringExtra(Constants.EXTRA_QUOTE_IMAGE);
                        setNewImageSource(uri);
                    }
            }
        }
    }

    @Override
    public void onDoneTextEdit(String quoteText) {
        mPhotoEditor.addText(quoteText, Color.WHITE);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        Bitmap bitmap = mPhotoEditorView.getOriginalBitmap();
        applyPhotoFilter(bitmap, filter, mBlurSeekBar.getProgress());
    }

    @Override
    public void onOriginalSelected() {
        CURRENT_PHOTO_FILTER = "None";
        Bitmap originalBitmap = mPhotoEditorView.getOriginalBitmap();
        // Will apply blur if it was applied earlier.
        applyBlur(originalBitmap, mBlurSeekBar.getProgress());
    }

    @Override
    public void onFontSelected(Typeface typeface) {
        TextView selectedText = (TextView) getSelectedViewByType(ViewType.TEXT);
        if (selectedText != null) {
            selectedText.setTypeface(typeface);
        }
    }

    @Override
    public void onFontRemove(final FavoriteFont favoriteFont) {

        final AlertDialog removeFontAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Font")
                .setMessage("Remove this font?")
                .setIcon(R.drawable.ic_error).create();

        removeFontAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getInstance(EditorActivity.this);
                        db.favoriteFontDao().removeFontFromFavorite(favoriteFont);
                    }
                });
            }
        });

        removeFontAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeFontAlertDialog.dismiss();
            }
        });

        removeFontAlertDialog.show();
    }

    @Override
    public void onStockImageSelected(final int drawable) {
        mEditorProgressBar.setVisibility(View.VISIBLE);
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mEditorProgressBar.setVisibility(View.INVISIBLE);
                mPhotoEditorView.getSource().setImageBitmap(bitmap);
                bindFilterThumbnails(bitmap);
                mPhotoEditorView.setOriginalBitmap(bitmap.copy(bitmap.getConfig(), true));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Toast.makeText(EditorActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                mEditorProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso picasso = Picasso.get();
        picasso.load(drawable).into(target);

        mPhotoEditorView.getSource().setTag(target);
    }

    @Override
    public void onViewSelected(ViewType viewType) {
        View selectedView = mPhotoEditor.getSelectedView();
        if (selectedView == null) {
            return;
        }

        switch (viewType) {
            case TEXT:
                enableImageControls(false);
                enableTextControls(true);
                TextView selectedText
                        = selectedView.findViewById(ja.burhanrashid52.photoeditor.R.id.tvPhotoEditorText);

                mPrimaryTextToolsFragment.setControlsAccordingToSelectedView(selectedText);
                mSecondaryTextToolsFragment.setControlsAccordingToSelectedView(selectedText);
                break;

            case IMAGE:
                enableTextControls(false);
                enableTextShadowControls(false);
                enableImageControls(true);

                ImageView selectedImage
                        = selectedView.findViewById(ja.burhanrashid52.photoeditor.R.id.imgPhotoEditorImage);
                int dimen = Math.max(selectedImage.getLayoutParams().height, selectedImage.getLayoutParams().width);
                int imageSizeProgress = dimen - Constants.MIN_IMAGE_SIZE;
                mImageSizeSeekBar.setProgress(imageSizeProgress);
                mImageOpacitySeekBar.setProgress(selectedImage.getImageAlpha() - Constants.MIN_IMAGE_OPACITY);
                break;
        }
    }

    @Override
    public void onParentViewClicked() {
        View selectedView = mPhotoEditor.getSelectedView();
        if (selectedView != null) {
            selectedView.findViewById(ja.burhanrashid52.photoeditor.R.id.frmBorder).setBackgroundResource(0);
            selectedView.findViewById(ja.burhanrashid52.photoeditor.R.id.imgPhotoEditorClose).setVisibility(View.INVISIBLE);
        }

        enableTextControls(false);
        enableTextShadowControls(false);
        enableImageControls(false);
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, final int colorCode) {
        TextDialogFragment textDialogFragment = new TextDialogFragment();
        textDialogFragment.setPreText(text);
        textDialogFragment.show(getSupportFragmentManager(), "ADD_TEXT");
        textDialogFragment.setOnTextDoneListener(new TextDialogFragment.OnDoneTextEditListener() {
            @Override
            public void onDoneTextEdit(String quoteText) {
                mPhotoEditor.editText(rootView, quoteText, colorCode);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        switch (viewType) {
            case TEXT:
                enableTextControls(true);
                enableTextShadowControls(false);
                enableImageControls(false);
                resetTextControls();
                break;

            case IMAGE:
                enableTextControls(false);
                enableTextShadowControls(false);
                enableImageControls(true);
                resetImageControls();
                break;
        }
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        switch (viewType) {
            case TEXT:
                enableTextControls(false);
                enableTextShadowControls(false);
            case IMAGE:
                enableImageControls(false);
        }
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {

    }

    @Override
    public void onSaveImage() {
        StorageUtils.saveBitmap(this, mPhotoEditor, new PhotoEditor.OnSaveListener() {
            @Override
            public void onSuccess(@NonNull String imagePath) {
                IS_SAVED = true;
                Toast.makeText(EditorActivity.this, "SAVED!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditorActivity.this, "Error saving!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onShareImage(Bitmap bitmap) {
        File file = null;
        boolean isSavingPreferred = PreferencesUtils.isSavingSharedPreferred(this);

        if (isSavingPreferred) {
            if (!StorageUtils.isStoragePermissionGranted(this)) {
                StorageUtils.tryRequestStoragePermissionForSaveAndShare(this);
                return;
            }

            file = StorageUtils.saveBitmap(this, mPhotoEditor, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }

        if (file != null) {
            StorageUtils.shareSavedBitmap(this, file);
        } else {
            StorageUtils.shareUnsavedBitmap(this, bitmap);
        }
    }

    // Helper functions below

    private void applyPhotoFilter(final Bitmap bitmap, final Filter filter, final int blur) {
        mEditorProgressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (filter != null && !filter.getName().equals(CURRENT_PHOTO_FILTER)) {

                    int width = bitmap.getWidth() - Math.round(bitmap.getWidth() * 0.05f);
                    int height = bitmap.getHeight() - Math.round(bitmap.getHeight() * 0.05f);

                    final Bitmap filteredBitmap = BmpUtils.blur(EditorActivity.this,
                            filter.processFilter(Bitmap.createScaledBitmap(bitmap, width, height, false)),
                            blur);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEditorProgressBar.setVisibility(View.INVISIBLE);
                            mPhotoEditorView.getSource()
                                    .setImageBitmap(BmpUtils.blur(EditorActivity.this, filteredBitmap, blur));
                            CURRENT_PHOTO_FILTER = filter.getName();
                        }
                    });
                }
            }
        };
        handler.post(runnable);
    }

    private void applyBlur(final Bitmap bitmap, final int blur) {
        mEditorProgressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int width = bitmap.getWidth() - Math.round(bitmap.getWidth() * 0.000f);
                int height = bitmap.getHeight() - Math.round(bitmap.getHeight() * 0.000f);

                final Bitmap blurredBitmap;

                if (CURRENT_PHOTO_FILTER.equals("None")) {
                    blurredBitmap = BmpUtils.blur(EditorActivity.this, bitmap, blur);
                } else {
                    blurredBitmap = BmpUtils.blur(EditorActivity.this,
                            BmpUtils.getFilterByName(EditorActivity.this, CURRENT_PHOTO_FILTER)
                                    .processFilter(Bitmap.createScaledBitmap(bitmap, width, height, false)),
                            blur);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mEditorProgressBar.setVisibility(View.INVISIBLE);
                        mPhotoEditorView.getSource().setImageBitmap(blurredBitmap);
                    }
                });
            }
        };
        handler.post(runnable);
    }

    private void initiateViews() {
        mPhotoEditorView = findViewById(R.id.photo_editor_view);

        mTextBottomSheet = findViewById(R.id.text_bottom_sheet);
        mImageBottomSheet = findViewById(R.id.image_bottom_sheet);
        mEffectsBottomSheet = findViewById(R.id.filter_bottom_sheet);
        mBackgroundBottomSheet = findViewById(R.id.background_bottom_sheet);

        mToolsRecyclerView = findViewById(R.id.tools_rv);
        mStockImagesRecyclerView = findViewById(R.id.stock_images_rv);
        mFiltersRecyclerView = findViewById(R.id.filters_rv1);

        mAddPhotoImageView = findViewById(R.id.add_image_imageview);
        mUndoImageView = findViewById(R.id.undo_imageview);
        mRedoImageView = findViewById(R.id.redo_imageview);
        mDoneEditImageView = findViewById(R.id.done_edit_imageview);
        mRotateBackgroundImageView = findViewById(R.id.rotate_background_imageview);
        mMoreBackgroundsImageView = findViewById(R.id.more_background_images_imageview);

        mEditorProgressBar = findViewById(R.id.editor_progress_bar);

        mTextControlsViewPager = findViewById(R.id.text_controls_viewpager);

        mBrightnessSeekBar = findViewById(R.id.brightness_seekbar);
        mBlurSeekBar = findViewById(R.id.blur_seekbar);
        mImageSizeSeekBar = findViewById(R.id.image_size_seekbar);
        mImageOpacitySeekBar = findViewById(R.id.image_opacity_seekbar);
    }

    private void hookRecyclerViewsToAdapters() {
        LinearLayoutManager toolsLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mToolsRecyclerView.setLayoutManager(toolsLayoutManager);
        mToolsRecyclerView.setHasFixedSize(true);
        mToolsRecyclerView.addItemDecoration(new ToolsItemDecoration(this));
        mToolsRecyclerView.setAdapter(new ToolsAdapter(this, this));

        LinearLayoutManager backgroundImagesLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mStockImagesRecyclerView.setLayoutManager(backgroundImagesLayoutManager);
        mStockImagesRecyclerView.setHasFixedSize(true);
        mStockImagesRecyclerView.addItemDecoration(new ThumbnailItemDecoration());
        mStockImagesRecyclerView.setAdapter(new StockImagesAdapter(this, this));

        LinearLayoutManager filtersLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mFiltersRecyclerView.setLayoutManager(filtersLayoutManager);
        mFiltersRecyclerView.setHasFixedSize(true);
        mFiltersRecyclerView.addItemDecoration(new ThumbnailItemDecoration());
        bindFilterThumbnails(mPhotoEditorView.getOriginalBitmap());
    }

    private void setNewImageSource(String uriString) {
        mEditorProgressBar.setVisibility(View.VISIBLE);
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mEditorProgressBar.setVisibility(View.INVISIBLE);
                mPhotoEditorView.getSource().setImageBitmap(bitmap);
                bindFilterThumbnails(bitmap);
                mPhotoEditorView.setOriginalBitmap(bitmap.copy(bitmap.getConfig(), true));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                mEditorProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(EditorActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso picasso = Picasso.get();
        picasso.setLoggingEnabled(true);
        picasso.load(uriString).resize(1400, 1400).centerInside().into(target);

        mPhotoEditorView.getSource().setTag(target);
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra(Constants.EXTRA_CAMERA_BUNDLE)) {
            Uri tempPhotoUri = intent.getParcelableExtra(Constants.EXTRA_CAMERA_BUNDLE);
            setNewImageSource(tempPhotoUri.toString());

        } else if (intent.hasExtra(Constants.EXTRA_PHOTOS_URI)) {
            String photoUri = intent.getStringExtra(Constants.EXTRA_PHOTOS_URI);
            setNewImageSource(photoUri);

        } else if (intent.hasExtra(Constants.EXTRA_COLOR_CODE)) {
            final int intColor = intent.getIntExtra(Constants.EXTRA_COLOR_CODE, Color.WHITE);
            mPhotoEditor.clearAllViews();

            Handler handler = new Handler();
            mEditorProgressBar.setVisibility(View.VISIBLE);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    final Bitmap coloredBitmap = BmpUtils.getColoredBitmap(1000, 1250, intColor);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPhotoEditorView.getSource().setImageBitmap(coloredBitmap);
                            bindFilterThumbnails(coloredBitmap);
                            mEditorProgressBar.setVisibility(View.INVISIBLE);
                            mPhotoEditorView.setOriginalBitmap(coloredBitmap.copy(coloredBitmap.getConfig(), true));
                        }
                    });
                }
            });

        } else if (intent.hasExtra(Constants.EXTRA_QUOTE_IMAGE)) {
            String uri = intent.getStringExtra(Constants.EXTRA_QUOTE_IMAGE);
            setNewImageSource(uri);

        } else if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String quote = intent.getStringExtra(Intent.EXTRA_TEXT);
            mTextDialogFragment = new TextDialogFragment();
            mTextDialogFragment.show(getSupportFragmentManager(), "ADD_TEXT");
            mTextDialogFragment.setOnTextDoneListener(EditorActivity.this);
            mTextDialogFragment.setPreText(quote);
        }
    }

    private void setupBottomSheetBehaviors() {
        mTextBottomSheetBehavior = BottomSheetBehavior.from(mTextBottomSheet);
        mBackgroundBottomSheetBehavior = BottomSheetBehavior.from(mBackgroundBottomSheet);
        mEffectBottomSheetBehavior = BottomSheetBehavior.from(mEffectsBottomSheet);
        mImageBottomSheetBehavior = BottomSheetBehavior.from(mImageBottomSheet);
    }

    private void expandBottomSheet(BottomSheetBehavior behavior) {
        BottomSheetBehavior currentBehavior = getCurrentBottomSheetBehavior();
        if (currentBehavior == null) {
            return;
        }
        currentBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private BottomSheetBehavior getCurrentBottomSheetBehavior() {
        if (mTextBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            return mTextBottomSheetBehavior;
        } else if (mBackgroundBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            return mBackgroundBottomSheetBehavior;
        } else if (mImageBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            return mImageBottomSheetBehavior;
        } else if (mEffectBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            return mEffectBottomSheetBehavior;
        } else {
            return null;
        }
    }

    private void showConfirmExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_error)
                .setTitle("Confirm Exit?")
                .setMessage("Exit without saving?");

        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StorageUtils.saveBitmap(EditorActivity.this, mPhotoEditor, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        Toast.makeText(EditorActivity.this, "SAVED!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
            }
        });

        builder.setNegativeButton("DON'T SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public View getSelectedViewByType(ViewType viewType) {
        View selectedView = mPhotoEditor.getSelectedView();
        View specificView = null;

        if (selectedView == null) {
            return null;
        } else if (selectedView.getTag() != viewType) {
            return null;
        } else if (selectedView.getTag() == ViewType.TEXT) {
            specificView = selectedView.findViewById(ja.burhanrashid52.photoeditor.R.id.tvPhotoEditorText);
        } else if (selectedView.getTag() == ViewType.IMAGE) {
            specificView = selectedView.findViewById(ja.burhanrashid52.photoeditor.R.id.imgPhotoEditorImage);
        }

        return specificView;
    }

    private void rotateBackground(int angle) {
        Bitmap bitmap = ((BitmapDrawable) mPhotoEditorView.getSource().getDrawable()).getBitmap();
        Bitmap rotatedBmp = BmpUtils.rotateImage(bitmap, angle);
        mPhotoEditorView.getSource().setImageBitmap(rotatedBmp);
        mPhotoEditorView.setOriginalBitmap(rotatedBmp.copy(rotatedBmp.getConfig(), true));
        if (CURRENT_ROTATION_ANGLE <= 180) {
            CURRENT_ROTATION_ANGLE = CURRENT_ROTATION_ANGLE + 90;
        } else {
            CURRENT_ROTATION_ANGLE = 0;
        }
    }

    private void enableTextControls(boolean toEnable) {
        mPrimaryTextToolsFragment.enablePrimaryTextControls(toEnable);
        mSecondaryTextToolsFragment.enableSecondaryTextControls(toEnable);
    }

    private void enableTextShadowControls(boolean toEnable) {
        mSecondaryTextToolsFragment.enableTextShadowControls(toEnable);
    }

    private void enableImageControls(boolean toEnable) {
        mImageSizeSeekBar.setEnabled(toEnable);
        mImageOpacitySeekBar.setEnabled(toEnable);
    }

    private void resetTextControls() {
        mPrimaryTextToolsFragment.resetPrimaryTextControls();
        mSecondaryTextToolsFragment.resetSecondaryTextControls();
    }

    private void resetImageControls() {
        ImageView selectedImage = (ImageView) getSelectedViewByType(ViewType.IMAGE);
        if (selectedImage != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Constants.DEFAULT_IMAGE_SIZE, Constants.DEFAULT_IMAGE_SIZE);
            selectedImage.setLayoutParams(params);
            mImageSizeSeekBar.setProgress(322);
            mImageOpacitySeekBar.setProgress(255);
        }
    }

    private void bindFilterThumbnails(final Bitmap originalBitmap) {
        final Context context = getApplication();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Bitmap thumbnailImage =
                        Bitmap.createScaledBitmap(originalBitmap, 80, 80, false);

                ThumbnailsManager.clearThumbs();

                List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

                for (Filter filter : filters) {
                    ThumbnailItem thumbnailItem = new ThumbnailItem();
                    thumbnailItem.image = thumbnailImage;
                    thumbnailItem.filter = filter;
                    ThumbnailsManager.addThumb(thumbnailItem);
                }

                ThumbnailItem originalThumb = new ThumbnailItem();
                originalThumb.image = thumbnailImage;
                originalThumb.filterName = "None";

                List<ThumbnailItem> thumbnailItems = ThumbnailsManager.processThumbs(context);
                thumbnailItems.add(0, originalThumb);

                FilterThumbnailsAdapter thumbnailsAdapter =
                        new FilterThumbnailsAdapter(thumbnailItems, EditorActivity.this);

                mFiltersRecyclerView.setAdapter(thumbnailsAdapter);

                thumbnailsAdapter.notifyDataSetChanged();
            }
        };
        handler.post(runnable);
    }
}