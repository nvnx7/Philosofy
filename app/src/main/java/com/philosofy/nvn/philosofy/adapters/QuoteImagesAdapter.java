package com.philosofy.nvn.philosofy.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.philosofy.nvn.philosofy.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class QuoteImagesAdapter extends RecyclerView.Adapter<QuoteImagesAdapter.DesignedQuoteViewHolder> {

    private File[] mImageFilesArray;
    private Picasso mPicasso;
    private OnQuoteImageClickedListener mOnQuoteImageClickedListener;

    public QuoteImagesAdapter(Context context, OnQuoteImageClickedListener onQuoteImageClickedListener,
                              File[] imageFilesArray) {
        mImageFilesArray = imageFilesArray;
        mPicasso = new Picasso.Builder(context).build();
        mOnQuoteImageClickedListener = onQuoteImageClickedListener;
    }

    public interface OnQuoteImageClickedListener {
        void onQuoteImageClicked(File file);
    }

    @NonNull
    @Override
    public DesignedQuoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemview_image, viewGroup, false);

        return new DesignedQuoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DesignedQuoteViewHolder designedQuoteViewHolder, int i) {
        File file = mImageFilesArray[i];
        mPicasso.load(file).centerCrop().resize(200,200).into(designedQuoteViewHolder.mPreviewImageView);
    }

    @Override
    public int getItemCount() {
        if (mImageFilesArray == null) {
            return 0;
        }
        return mImageFilesArray.length;
    }

    public void swapData(File[] files) {
        if (mImageFilesArray != null) {
            mImageFilesArray = null;
        }
        mImageFilesArray = files;
        notifyDataSetChanged();
    }

    class DesignedQuoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mPreviewImageView;

        DesignedQuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            mPreviewImageView = itemView.findViewById(R.id.preview_imageview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnQuoteImageClickedListener.onQuoteImageClicked(mImageFilesArray[getAdapterPosition()]);
        }
    }

}
