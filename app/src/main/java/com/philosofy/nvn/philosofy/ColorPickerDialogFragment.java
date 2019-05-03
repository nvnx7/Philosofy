package com.philosofy.nvn.philosofy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.custom.CustomFlag;
import com.philosofy.nvn.philosofy.utils.Constants;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

public class ColorPickerDialogFragment extends DialogFragment {

    private int currentlySelectedColor = Color.WHITE;

    private ColorPickerView mColorPickerView;
    private BrightnessSlideBar mBrightnessSlideBar;
    private TextView mSelectColorTextView;
    private TextView mCancelColorChooserTextView;

    private OnColorChosenListener mOnColorChosenListener;

    public interface OnColorChosenListener{
        void onColorChosen(int color);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;

            int w = (int) Math.round(0.95*width);
            int h = ViewGroup.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(w, h);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_color_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mColorPickerView = view.findViewById(R.id.color_picker_view);
        mBrightnessSlideBar = view.findViewById(R.id.brightness_slide_bar);
        mSelectColorTextView = view.findViewById(R.id.select_color_textview);
        mCancelColorChooserTextView = view.findViewById(R.id.cancel_color_textview);

        CustomFlag customFlag = new CustomFlag(getActivity(), R.layout.color_chooser_flagview);
        customFlag.setFlagMode(FlagMode.ALWAYS);
        mColorPickerView.setFlagView(customFlag);
        mColorPickerView.attachBrightnessSlider(mBrightnessSlideBar);
        mColorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                currentlySelectedColor = envelope.getColor();
            }
        });

        mSelectColorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnColorChosenListener.onColorChosen(currentlySelectedColor);
                dismiss();
            }
        });

        mCancelColorChooserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public void setOnColorChosenListener(OnColorChosenListener onColorChosenListener) {
        mOnColorChosenListener = onColorChosenListener;
    }
}
