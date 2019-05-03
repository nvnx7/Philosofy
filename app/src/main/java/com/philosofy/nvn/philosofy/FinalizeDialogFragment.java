package com.philosofy.nvn.philosofy;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.utils.BmpUtils;
import com.philosofy.nvn.philosofy.utils.PreferencesUtils;
import com.philosofy.nvn.philosofy.utils.StorageUtils;

public class FinalizeDialogFragment extends DialogFragment {

    private TextView mSaveEditedImageTextView;
    private TextView mShareEditedImageTextView;
    private ImageView mCloseFinalizeImageView;
    private ImageView mFinalizedEditImageView;
    private FloatingActionButton mSaveEditedImageFab;
    private FloatingActionButton mShareEditedImageFab;
    private ProgressBar mFinalizeProgressBar;

    private OnFinalizeImageCallback mOnFinalizeImageCallback;

    private View finalizedView = null;

    public interface OnFinalizeImageCallback {
        void onSaveImage();
        void onShareImage(Bitmap bitmap);
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
        return inflater.inflate(R.layout.dialog_fragment_finalize, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSaveEditedImageTextView = view.findViewById(R.id.save_finalized_image_textview);
        mShareEditedImageTextView = view.findViewById(R.id.share_finalized_image_textview);
        mCloseFinalizeImageView = view.findViewById(R.id.close_finalize_imageview);
        mFinalizedEditImageView = view.findViewById(R.id.finalized_image_imageview);
        mSaveEditedImageFab = view.findViewById(R.id.save_edited_image_fab);
        mShareEditedImageFab = view.findViewById(R.id.share_edited_image_fab);
        mFinalizeProgressBar = view.findViewById(R.id.finalizing_progress_bar);

        startCreatingFinalImage(finalizedView);

        mCloseFinalizeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinalizeDialogFragment.this.dismiss();
            }
        });

        mSaveEditedImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFinalImageToStorage();
            }
        });

        mShareEditedImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) mFinalizedEditImageView.getDrawable()).getBitmap();
                mOnFinalizeImageCallback.onShareImage(bitmap);
                // Close the dialog if permission granted or user chooses not to save prior share
                if (StorageUtils.isStoragePermissionGranted(getContext())
                        || !PreferencesUtils.isSavingSharedPreferred(getContext())) {
                    dismiss();
                }
            }
        });
    }

    private void startCreatingFinalImage(final View view) {
        showLoading();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Bitmap bitmap = BmpUtils.getBitmapFromView(getActivity().getWindow(), view,
                    new PixelCopy.OnPixelCopyFinishedListener() {
                        @Override
                        public void onPixelCopyFinished(int copyResult) {
                            showEditedImage();
                            mSaveEditedImageFab.show();
                            mShareEditedImageFab.show();
                            mSaveEditedImageTextView.setVisibility(View.VISIBLE);
                            mShareEditedImageTextView.setVisibility(View.VISIBLE);
                        }
                    });
            mFinalizedEditImageView.setImageBitmap(bitmap);
        } else {
            AsyncTask<Void, Void, Bitmap> finalizeAsyncTask = new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected void onPreExecute() {
                    showLoading();
                }

                @Override
                protected Bitmap doInBackground(Void... voids) {
                    return BmpUtils.getBitmapFromView(view);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    mFinalizedEditImageView.setImageBitmap(bitmap);
                    showEditedImage();
                    mSaveEditedImageFab.show();
                    mShareEditedImageFab.show();
                    mSaveEditedImageTextView.setVisibility(View.VISIBLE);
                    mShareEditedImageTextView.setVisibility(View.VISIBLE);
                }
            };

            finalizeAsyncTask.execute();
        }
    }

    private void saveFinalImageToStorage() {
        Context context = getContext();
        if (!StorageUtils.isStoragePermissionGranted(context)) {
            StorageUtils.tryRequestStoragePermissionForSaving(context);
        } else {
            mOnFinalizeImageCallback.onSaveImage();
            dismiss();
        }
    }

    private void showEditedImage() {
        mFinalizeProgressBar.setVisibility(View.INVISIBLE);
        mFinalizedEditImageView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mFinalizeProgressBar.setVisibility(View.VISIBLE);
        mFinalizedEditImageView.setVisibility(View.INVISIBLE);
    }

    public void setFinalizedView(View view) {
        finalizedView = view;
    }

    public void setOnSaveImageCallback(OnFinalizeImageCallback onFinalizeImageCallback) {
        mOnFinalizeImageCallback = onFinalizeImageCallback;
    }
}
