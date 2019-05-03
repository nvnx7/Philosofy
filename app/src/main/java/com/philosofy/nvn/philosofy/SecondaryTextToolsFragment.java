package com.philosofy.nvn.philosofy;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.utils.Constants;

import ja.burhanrashid52.photoeditor.ViewType;

public class SecondaryTextToolsFragment extends Fragment
        implements View.OnClickListener {

    private static final String TAG = SecondaryTextToolsFragment.class.getSimpleName();
    private EditorActivity mEditorActivity;

    private Switch mTextShadowSwitch;

    private SeekBar mTextShadowRadiusSeekBar;
    private SeekBar mShadowXPosSeekBar;
    private SeekBar mShadowYPosSeekBar;

    private ImageView mTextAlignmentImageView;
    private ImageView mShadowColorImageView;
    private ImageView mBackButtonImageView;

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListenerForTextSeekBars
            = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            TextView selectedText = (TextView) mEditorActivity.getSelectedViewByType(ViewType.TEXT);
            if (selectedText == null) {
                return;
            }

            float radius = selectedText.getShadowRadius();
            float dx = selectedText.getShadowDx();
            float dy = selectedText.getShadowDy();
            int color = selectedText.getShadowColor();

            switch (seekBar.getId()) {
                case R.id.shadow_radius_seekbar:
                    radius = progress + Constants.MIN_SHADOW_RADIUS;
                    selectedText.setShadowLayer(radius, dx, dy, color);
                    break;

                case R.id.shadow_xpos_seekbar:
                    dx = progress + Constants.MIN_SHADOW_DX;
                    selectedText.setShadowLayer(radius, dx, dy, color);
                    break;

                case R.id.shadow_ypos_seekbar:
                    dy = progress + Constants.MIN_SHADOW_DY;
                    selectedText.setShadowLayer(radius, dx, dy, color);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_control_secondary, container, false);

        mEditorActivity = (EditorActivity) getActivity();
        initiateViews(view);

        mTextAlignmentImageView.setOnClickListener(this);
        mBackButtonImageView.setOnClickListener(mEditorActivity);

        mShadowColorImageView.setOnClickListener(this);
        mTextShadowRadiusSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListenerForTextSeekBars);
        mShadowXPosSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListenerForTextSeekBars);
        mShadowYPosSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListenerForTextSeekBars);
        mTextShadowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView selectedText = (TextView) mEditorActivity.getSelectedViewByType(ViewType.TEXT);
                if (selectedText != null) {
                    if (isChecked) {
                        enableTextShadowControls(true);
                        int color = ((ColorDrawable) mShadowColorImageView.getBackground()).getColor();
                        int radius = mTextShadowRadiusSeekBar.getProgress() + 10;
                        int dx = mShadowXPosSeekBar.getProgress() - 50;
                        int dy = mShadowYPosSeekBar.getProgress() - 50;
                        selectedText.setShadowLayer(radius, dx, dy, color);
                    } else {
                        selectedText.setShadowLayer(0, 0, 0, 0);
                        enableTextShadowControls(false);
                    }
                }
            }
        });

        enableSecondaryTextControls(false);
        enableTextShadowControls(false);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.text_alignment_imageview:
                TextView selectedText = (TextView) mEditorActivity.getSelectedViewByType(ViewType.TEXT);
                if (selectedText != null) {
                    int alignment = getNextAlignment(selectedText.getTextAlignment(), v);
                    int gravity;
                    if (alignment == View.TEXT_ALIGNMENT_TEXT_START) gravity = Gravity.START;
                    else if (alignment == View.TEXT_ALIGNMENT_TEXT_END) gravity = Gravity.END;
                    else gravity = Gravity.CENTER_HORIZONTAL;

                    selectedText.setTextAlignment(alignment);
                    selectedText.setGravity(gravity);
                }
                break;

            case R.id.shadow_color_imageview:
                ColorPickerDialogFragment colorPickerDialogFragment = new ColorPickerDialogFragment();
                colorPickerDialogFragment.setOnColorChosenListener(new ColorPickerDialogFragment.OnColorChosenListener() {
                    @Override
                    public void onColorChosen(int color) {
                        mShadowColorImageView.setBackgroundColor(color);

                        TextView selectedText = (TextView) mEditorActivity.getSelectedViewByType(ViewType.TEXT);
                        if (selectedText == null) {
                            return;
                        }
                        selectedText.setShadowLayer(selectedText.getShadowRadius(), selectedText.getShadowDx(),
                                selectedText.getShadowDy(), color);
                    }
                });
                colorPickerDialogFragment.show(mEditorActivity.getSupportFragmentManager(), "SHADOW_COLOR");
                break;
        }
    }

    public void setControlsAccordingToSelectedView(TextView selectedText) {
        int shadowRadius = (int) selectedText.getShadowRadius() - Constants.MIN_SHADOW_RADIUS;
        int shadowColor = selectedText.getShadowColor();
        int shadowDx = (int) selectedText.getShadowDx() - Constants.MIN_SHADOW_DX;
        int shadowDy = (int) selectedText.getShadowDy() - Constants.MIN_SHADOW_DY;

        if (shadowRadius <= 0) {
            mTextShadowSwitch.setChecked(false);
            enableTextShadowControls(false);
        } else {
            enableTextShadowControls(true);

            mTextShadowSwitch.setChecked(true);
            mTextShadowRadiusSeekBar.setProgress(shadowRadius);
            mShadowColorImageView.setBackgroundColor(shadowColor);
            mShadowXPosSeekBar.setProgress(shadowDx);
            mShadowYPosSeekBar.setProgress(shadowDy);
        }

        int alignment = selectedText.getTextAlignment();
        switch (alignment) {
            case View.TEXT_ALIGNMENT_CENTER:
                mTextAlignmentImageView.setImageResource(R.drawable.ic_align_center);
                break;
            case View.TEXT_ALIGNMENT_TEXT_END:
                mTextAlignmentImageView.setImageResource(R.drawable.ic_align_right);
                break;
            case View.TEXT_ALIGNMENT_TEXT_START:
                mTextAlignmentImageView.setImageResource(R.drawable.ic_align_left);
                break;
            default:
                mTextAlignmentImageView.setImageResource(R.drawable.ic_align_center);
        }
    }

    public void enableSecondaryTextControls(boolean toEnable) {
        mTextAlignmentImageView.setEnabled(toEnable);
        mTextShadowSwitch.setEnabled(toEnable);
    }

    public void enableTextShadowControls(boolean toEnable) {
        mTextShadowRadiusSeekBar.setEnabled(toEnable);
        mShadowXPosSeekBar.setEnabled(toEnable);
        mShadowYPosSeekBar.setEnabled(toEnable);
        mShadowColorImageView.setEnabled(toEnable);
    }

    public void resetSecondaryTextControls() {
        mTextShadowSwitch.setChecked(false);
        mTextShadowRadiusSeekBar.setProgress(20);
        mShadowXPosSeekBar.setProgress(50);
        mShadowYPosSeekBar.setProgress(50);
    }

    private int getNextAlignment(int currentAlignment, View clickedView) {
        ImageView textAlignmentImageView = (ImageView) clickedView;

        switch (currentAlignment) {
            case View.TEXT_ALIGNMENT_CENTER:
                textAlignmentImageView.setImageResource(R.drawable.ic_align_left);
                return View.TEXT_ALIGNMENT_TEXT_START;

            case View.TEXT_ALIGNMENT_TEXT_START:
                textAlignmentImageView.setImageResource(R.drawable.ic_align_right);
                return View.TEXT_ALIGNMENT_TEXT_END;

            case View.TEXT_ALIGNMENT_TEXT_END:
                textAlignmentImageView.setImageResource(R.drawable.ic_align_center);
                return View.TEXT_ALIGNMENT_CENTER;

            default:
                textAlignmentImageView.setImageResource(R.drawable.ic_align_center);
                return View.TEXT_ALIGNMENT_CENTER;
        }
    }

    private void initiateViews(View view) {
        mTextShadowSwitch = view.findViewById(R.id.text_shadow_switch);
        mTextShadowRadiusSeekBar = view.findViewById(R.id.shadow_radius_seekbar);
        mShadowXPosSeekBar = view.findViewById(R.id.shadow_xpos_seekbar);
        mShadowYPosSeekBar = view.findViewById(R.id.shadow_ypos_seekbar);
        mTextAlignmentImageView = view.findViewById(R.id.text_alignment_imageview);
        mBackButtonImageView = view.findViewById(R.id.previous_controls_imageview);
        mShadowColorImageView = view.findViewById(R.id.shadow_color_imageview);
    }
}
