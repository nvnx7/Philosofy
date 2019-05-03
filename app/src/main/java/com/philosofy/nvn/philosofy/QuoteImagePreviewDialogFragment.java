package com.philosofy.nvn.philosofy;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.philosofy.nvn.philosofy.utils.Constants;
import com.philosofy.nvn.philosofy.utils.StorageUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class QuoteImagePreviewDialogFragment extends DialogFragment {

    private TextView mShareOrEditTextView;
    private ImageView mCloseDialogImageView;
    private ImageView mDesignedQuoteImageView;
    private FloatingActionButton mDeleteImageFab;
    private FloatingActionButton mShareOrEditImageFab;
    private Picasso mPicasso;

    private QuoteImageCallbacks mQuoteImageCallbacks;

    public interface QuoteImageCallbacks {
        void onQuoteImageDelete(File file);
        void onQuoteImageEdit(File file);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_quote_image_preview, container, false);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initiateViews(view);
        mPicasso = new Picasso.Builder(getContext()).build();
        final File file = (File) getArguments().getSerializable(Constants.KEY_BUNDLE_DESIGNED_QUOTE);

        if (getTag().equals("DOWNLOADED_IMAGE")) {
            mShareOrEditImageFab.setImageResource(R.drawable.ic_pen);
            mShareOrEditTextView.setText(getString(R.string.label_quote_image));
            mShareOrEditImageFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mQuoteImageCallbacks.onQuoteImageEdit(file);
                }
            });

        } else {
            mShareOrEditImageFab.setImageResource(R.drawable.ic_share);
            mShareOrEditTextView.setText(getString(R.string.label_share_quote_image));
            mShareOrEditImageFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StorageUtils.shareSavedBitmap(getContext(), file);
                    dismiss();
                }
            });
        }

        if (file != null) {
            mPicasso.load(file).into(mDesignedQuoteImageView, new Callback() {
                @Override
                public void onSuccess() {
                    mDeleteImageFab.show();
                    mShareOrEditImageFab.show();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getContext(), "ERROR!", Toast.LENGTH_SHORT).show();
                    QuoteImagePreviewDialogFragment.this.dismiss();
                }
            });

            mDeleteImageFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mQuoteImageCallbacks.onQuoteImageDelete(file);
                    QuoteImagePreviewDialogFragment.this.dismiss();
                }
            });
        }

        mCloseDialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }



    public void setOnQuoteImageDeleteListener(QuoteImageCallbacks quoteImageCallbacks) {
        mQuoteImageCallbacks = quoteImageCallbacks;
    }

    private void initiateViews(View view) {
        mCloseDialogImageView = view.findViewById(R.id.close_design_preview_imageview);
        mDesignedQuoteImageView = view.findViewById(R.id.designed_quote_imageview);
        mDeleteImageFab = view.findViewById(R.id.delete_image_fab);
        mShareOrEditImageFab = view.findViewById(R.id.share_image_fab);
        mShareOrEditTextView = view.findViewById(R.id.share_quote_image_textview);
    }
}
