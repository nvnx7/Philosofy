package com.philosofy.nvn.philosofy.custom;

import android.content.Context;
import android.widget.ImageView;

import com.philosofy.nvn.philosofy.R;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.flag.FlagView;

public class CustomFlag extends FlagView {
    private ImageView mFlagImageView;

    public CustomFlag(Context context, int layout) {
        super(context, layout);
        mFlagImageView = findViewById(R.id.flag_imageview);
    }

    @Override
    public void onRefresh(ColorEnvelope colorEnvelope) {
        mFlagImageView.setColorFilter(colorEnvelope.getColor());
    }
}
