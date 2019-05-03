package com.philosofy.nvn.philosofy.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.R;
import com.philosofy.nvn.philosofy.database.DownloadableImage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DownloadableImagesAdapter extends RecyclerView.Adapter<DownloadableImagesAdapter.BackgroundsViewHolder> {
    private static final String TAG = DownloadableImagesAdapter.class.getSimpleName();

    private List<DownloadableImage> mDownloadableImages;
    private OnDownloadableImageClickListener mOnDownloadableImageClickListener;
    private Picasso mPicasso;

    public DownloadableImagesAdapter(Context context, OnDownloadableImageClickListener onDownloadableImageClickListener,
                                     List<DownloadableImage> downloadableImages) {
        mPicasso = new Picasso.Builder(context).build();
        mDownloadableImages = downloadableImages;
        mOnDownloadableImageClickListener = onDownloadableImageClickListener;
    }

    public interface OnDownloadableImageClickListener {
        void onDownloadableImageClick(String largeImageUrl);
    }

    @NonNull
    @Override
    public BackgroundsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemview_image, viewGroup, false);

        return new BackgroundsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BackgroundsViewHolder backgroundsViewHolder, int i) {
        String previewImageUrl = mDownloadableImages.get(i).getPreviewImageUrl();
        mPicasso.load(previewImageUrl).placeholder(R.drawable.ic_launcher_foreground).into(backgroundsViewHolder.mImagePreview);
    }

    @Override
    public int getItemCount() {
        if (mDownloadableImages == null) {
            return 0;
        }
        return mDownloadableImages.size();
    }

    public void swapData(List<DownloadableImage> downloadableImages) {
        if (mDownloadableImages != null) {
            mDownloadableImages = null;
        }
        mDownloadableImages = downloadableImages;
        notifyDataSetChanged();
    }

    public void addImagesData(List<DownloadableImage> downloadableImages) {
        if (mDownloadableImages != null && !mDownloadableImages.isEmpty()) {
            int positionStart = mDownloadableImages.size();
            mDownloadableImages = downloadableImages;
            notifyItemRangeInserted(positionStart, downloadableImages.size());
        } else {
            swapData(downloadableImages);
        }
    }

    class BackgroundsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImagePreview;

        BackgroundsViewHolder(@NonNull View itemView) {
            super(itemView);
            mImagePreview = itemView.findViewById(R.id.preview_imageview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnDownloadableImageClickListener.onDownloadableImageClick(mDownloadableImages.get(getAdapterPosition()).getLargeImageUrl());
        }
    }
}
