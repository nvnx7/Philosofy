package com.philosofy.nvn.philosofy;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.utils.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class LargeImagePreviewDialogFragment extends DialogFragment {

    private static final String TAG = LargeImagePreviewDialogFragment.class.getSimpleName();

    private ImageView mBackgroundImageView;
    private FloatingActionButton mSaveImageFab;
    private ImageView mClosePreviewImageView;
    private FloatingActionButton mEditImageFab;
    private TextView mErrorTextView;
    private TextView mSaveImageTextView;
    private TextView mEditImageTextView;

    private ProgressBar mImageLoadingProgressBar;

    private LargeImagePreviewCallback mLargeImagePreviewCallback;

    public interface LargeImagePreviewCallback {
        void onSaveDownloadableImage(Bitmap bitmap);
        void onEditDownloadableImage(String url);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_large_image_preview, container, false);
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

        final String largeImageUrl = getArguments().getString(Constants.KEY_LARGE_IMAGE_URL);

        Picasso.get().load(largeImageUrl).into(mBackgroundImageView, new Callback() {
            @Override
            public void onSuccess() {
                mImageLoadingProgressBar.setVisibility(View.INVISIBLE);
                mEditImageFab.show();
                mEditImageTextView.setVisibility(View.VISIBLE);
                mSaveImageFab.show();
                mSaveImageTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                mEditImageFab.setEnabled(false);
                mSaveImageFab.setEnabled(false);
                mImageLoadingProgressBar.setVisibility(View.INVISIBLE);
                mErrorTextView.setVisibility(View.VISIBLE);
            }
        });

        mEditImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLargeImagePreviewCallback.onEditDownloadableImage(largeImageUrl);
            }
        });

        mSaveImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) mBackgroundImageView.getDrawable()).getBitmap();
                mLargeImagePreviewCallback.onSaveDownloadableImage(bitmap);
            }
        });

        mClosePreviewImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setLargeImagePreviewCallback(LargeImagePreviewCallback largeImagePreviewCallback) {
        mLargeImagePreviewCallback = largeImagePreviewCallback;
    }

    private void initiateViews(View view) {
        mBackgroundImageView = view.findViewById(R.id.download_image_preview_imageview);
        mSaveImageFab = view.findViewById(R.id.save_image_fab);
        mClosePreviewImageView = view.findViewById(R.id.close_preview_imageview);
        mEditImageFab = view.findViewById(R.id.quote_image_fab);
        mImageLoadingProgressBar = view.findViewById(R.id.image_loading_progress_bar);
        mErrorTextView = view.findViewById(R.id.error_image_view);
        mSaveImageTextView = view.findViewById(R.id.save_image_textview);
        mEditImageTextView = view.findViewById(R.id.quote_image_textview);
    }

}
