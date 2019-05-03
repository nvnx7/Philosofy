package com.philosofy.nvn.philosofy.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.R;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class FilterThumbnailsAdapter extends RecyclerView.Adapter<FilterThumbnailsAdapter.ThumbnailViewHolder> {

    private static final String TAG = FilterThumbnailsAdapter.class.getSimpleName();

    private List<ThumbnailItem> mThumbnailList;
    private FilterThumbnailCallback mFilterThumbnailCallback;

    private int lastPosition = -1;

    public FilterThumbnailsAdapter(List<ThumbnailItem> thumbnailList,
                                   FilterThumbnailCallback filterThumbnailCallback) {
        mThumbnailList = thumbnailList;
        mFilterThumbnailCallback = filterThumbnailCallback;
    }

    public interface FilterThumbnailCallback {
        void onFilterSelected(Filter filter);

        void onOriginalSelected();
    }

    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemview_filters, viewGroup, false);

        return new ThumbnailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailViewHolder thumbnailViewHolder, final int i) {

        final ThumbnailItem thumbnailItem = mThumbnailList.get(i);

        if (thumbnailViewHolder.getItemViewType() == 0) {
            thumbnailViewHolder.mFilterThumbnail.setImageBitmap(thumbnailItem.image);
            thumbnailViewHolder.mFilterThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbnailViewHolder.mFilterName.setText(thumbnailItem.filterName);
            thumbnailViewHolder.mFilterThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onCLick for fetching 'None' filter");
                    if (lastPosition != i) {
                        mFilterThumbnailCallback.onOriginalSelected();
                        lastPosition = i;
                    }
                }
            });
            return;
        }

        thumbnailViewHolder.mFilterThumbnail.setImageBitmap(thumbnailItem.image);
        thumbnailViewHolder.mFilterThumbnail.setScaleType(ImageView.ScaleType.FIT_START);
        thumbnailViewHolder.mFilterName.setText(thumbnailItem.filter.getName());

        thumbnailViewHolder.mFilterThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "OnCLick Called for: " + thumbnailItem.filter.getName());
                Log.i(TAG, " lastposition: " + lastPosition);
                if (i != lastPosition ) {
                    Log.i(TAG,"Setting the filter now!");
                    mFilterThumbnailCallback.onFilterSelected(thumbnailItem.filter);
                    lastPosition = i;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mThumbnailList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    class ThumbnailViewHolder extends RecyclerView.ViewHolder {
        ImageView mFilterThumbnail;
        TextView mFilterName;

        ThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            mFilterThumbnail = itemView.findViewById(R.id.filter_thumbnail_imageview);
            mFilterName = itemView.findViewById(R.id.filter_name_textview);
        }
    }
}
