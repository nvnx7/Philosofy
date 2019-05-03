package com.philosofy.nvn.philosofy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.custom.ColorsItemDecoration;
import com.philosofy.nvn.philosofy.database.FavoriteFont;
import com.philosofy.nvn.philosofy.database.FavoriteFontsViewModel;
import com.philosofy.nvn.philosofy.adapters.ColorsAdapter;
import com.philosofy.nvn.philosofy.adapters.FontsAdapter;
import com.philosofy.nvn.philosofy.utils.Constants;
import com.philosofy.nvn.philosofy.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.ViewType;


public class PrimaryTextToolsFragment extends Fragment
        implements View.OnClickListener,
        ColorsAdapter.OnColorSelectedListener {

    private static final String TAG = PrimaryTextToolsFragment.class.getSimpleName();

    private EditorActivity mEditorActivity;

    private RecyclerView mFontsRecyclerView;
    private RecyclerView mTextColorsRecyclerView;

    private ColorsAdapter textColorsAdapter;
    private FontsAdapter fontsAdapter = null;

    private ImageView mAddTextImageView;
    private ImageView mMoreColorsImageView;
    private ImageView mCurrentTextColorImageView;
    private ImageView mMoreFontsImageView;
    private ImageView mMoreControlsImageView;

    private SeekBar mTextSizeSeekBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_control_primary, container, false);

        mEditorActivity = (EditorActivity) getActivity();
        initiateViews(view);
        hookRecyclerViewsToAdapters();

        mAddTextImageView.setOnClickListener(this);
        mMoreColorsImageView.setOnClickListener(this);
        mMoreFontsImageView.setOnClickListener(this);
        mMoreControlsImageView.setOnClickListener(mEditorActivity);
        mTextSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView selectedText = (TextView) mEditorActivity.getSelectedViewByType(ViewType.TEXT);
                if (selectedText != null) {
                    int size = (progress + Constants.MIN_TEXT_SIZE);
                    selectedText.setTextSize(size);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        enablePrimaryTextControls(false);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.add_text_image_view:
                TextDialogFragment textDialogFragment = new TextDialogFragment();
                textDialogFragment.show(mEditorActivity.getSupportFragmentManager(), "ADD_TEXT");
                textDialogFragment.setOnTextDoneListener(mEditorActivity);
                break;

            case R.id.more_colors_imageview:
                ColorPickerDialogFragment colorPickerDialogFragment = new ColorPickerDialogFragment();
                colorPickerDialogFragment.setOnColorChosenListener(new ColorPickerDialogFragment.OnColorChosenListener() {
                    @Override
                    public void onColorChosen(int color) {
                        TextView selectedText = (TextView) mEditorActivity.getSelectedViewByType(ViewType.TEXT);
                        if (selectedText != null) {
                            selectedText.setTextColor(color);
                            mCurrentTextColorImageView.setColorFilter(color);
                            PreferencesUtils.updateRecentColors(mEditorActivity, color);
                            textColorsAdapter.updateRecentColors();
                        }
                    }
                });
                colorPickerDialogFragment.show(mEditorActivity.getSupportFragmentManager(), "TEXT_COLOR");
                break;

            case R.id.more_fonts_imageview:
                Intent moreFontsIntent = new Intent(mEditorActivity, DownloadableFontsActivity.class);
                mEditorActivity.startActivityForResult(moreFontsIntent, Constants.REQUEST_CODE_DOWNLOADABLE_FONT);
                break;
        }
    }

    // Color from pre - defined material colors.
    @Override
    public void onTextColorSelected(int colorCode) {
        TextView selectedText = (TextView) mEditorActivity.getSelectedViewByType(ViewType.TEXT);
        if (selectedText != null) {
            selectedText.setTextColor(colorCode);
            mCurrentTextColorImageView.setColorFilter(colorCode);
        }
    }

    public void setControlsAccordingToSelectedView(TextView selectedText) {
        int textSizeProgress = (getProgressBySize(selectedText.getTextSize()) - Constants.MIN_TEXT_SIZE);
        mTextSizeSeekBar.setProgress(textSizeProgress);
        mCurrentTextColorImageView.setColorFilter(selectedText.getCurrentTextColor());
    }

    public void enablePrimaryTextControls(boolean toEnable) {
        mTextSizeSeekBar.setEnabled(toEnable);
        mMoreColorsImageView.setEnabled(toEnable);
    }

    public void resetPrimaryTextControls() {
        mTextSizeSeekBar.setProgress(18);
    }

    private int getProgressBySize(float pixelSize) {
        float spSize = pixelSize / getResources().getDisplayMetrics().scaledDensity;
        return (int) (spSize * (30 / 18));
    }

    private void initiateViews(View view) {
        mFontsRecyclerView = view.findViewById(R.id.fonts_rv1);
        mTextColorsRecyclerView = view.findViewById(R.id.text_colors_rv1);
        mAddTextImageView = view.findViewById(R.id.add_text_image_view);
        mTextSizeSeekBar = view.findViewById(R.id.text_size_seek_bar);

        mAddTextImageView = view.findViewById(R.id.add_text_image_view);
        mMoreColorsImageView = view.findViewById(R.id.more_colors_imageview);
        mTextSizeSeekBar = view.findViewById(R.id.text_size_seek_bar);
        mMoreFontsImageView = view.findViewById(R.id.more_fonts_imageview);

        mMoreControlsImageView = view.findViewById(R.id.more_controls_imageview);
        mCurrentTextColorImageView = view.findViewById(R.id.current_text_color_imageview);
    }

    private void hookRecyclerViewsToAdapters() {
        LinearLayoutManager fontsLayoutManager= new LinearLayoutManager(mEditorActivity,
                LinearLayoutManager.HORIZONTAL, false);

        mFontsRecyclerView.setLayoutManager(fontsLayoutManager);
        setupFavoriteFonts();

        LinearLayoutManager textColorsLayoutManager = new LinearLayoutManager(mEditorActivity,
                LinearLayoutManager.HORIZONTAL, false);
        mTextColorsRecyclerView.setLayoutManager(textColorsLayoutManager);
        mTextColorsRecyclerView.setHasFixedSize(true);
        mTextColorsRecyclerView.addItemDecoration(new ColorsItemDecoration());

        textColorsAdapter = new ColorsAdapter(mEditorActivity, this);
        mTextColorsRecyclerView.setAdapter(textColorsAdapter);
    }

    private void setupFavoriteFonts() {
        FavoriteFontsViewModel favoriteFontsViewModel
                = ViewModelProviders.of(this).get(FavoriteFontsViewModel.class);

        LiveData<List<FavoriteFont>> favoriteFontsLiveData = favoriteFontsViewModel.getFavoriteFontsLiveData();

        if (fontsAdapter == null) {
            fontsAdapter = new FontsAdapter(getContext(), (FontsAdapter.FavoriteFontsCallback) getContext(), null);
            mFontsRecyclerView.setAdapter(fontsAdapter);
        }

        favoriteFontsLiveData.observe(this, new Observer<List<FavoriteFont>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteFont> favoriteFonts) {
                fontsAdapter.swapData((ArrayList<FavoriteFont>) favoriteFonts);
            }
        });
    }
}
