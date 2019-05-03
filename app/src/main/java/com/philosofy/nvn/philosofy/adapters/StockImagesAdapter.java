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

import java.util.ArrayList;

public class StockImagesAdapter extends RecyclerView.Adapter<StockImagesAdapter.BackgroundViewHolder> {

    private Context mContext;
    private ArrayList<Integer> mStockImagesList;
    private OnStockImageSelectedListener mOnStockImageSelectedListener;
    private Picasso mPicasso;

    public StockImagesAdapter(Context context, OnStockImageSelectedListener onStockImageSelectedListener) {
        mContext = context;
        mOnStockImageSelectedListener = onStockImageSelectedListener;
        mStockImagesList = new ArrayList<>();
        mPicasso = new Picasso.Builder(context).build();

        mStockImagesList.add(R.drawable.stock_img_1);
        mStockImagesList.add(R.drawable.stock_img_2);
        mStockImagesList.add(R.drawable.stock_img_3);
        mStockImagesList.add(R.drawable.stock_img_4);
        mStockImagesList.add(R.drawable.stock_img_5);
        mStockImagesList.add(R.drawable.stock_img_6);
        mStockImagesList.add(R.drawable.stock_img_7);
        mStockImagesList.add(R.drawable.stock_img_8);
        mStockImagesList.add(R.drawable.stock_img_9);
        mStockImagesList.add(R.drawable.stock_img_10);
    }

    public interface OnStockImageSelectedListener{
        void onStockImageSelected(int drawable);
    }

    @NonNull
    @Override
    public BackgroundViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemview_stock_images, viewGroup, false);

        return new BackgroundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BackgroundViewHolder backgroundViewHolder, int i) {
        mPicasso.load(mStockImagesList.get(i)).resize(100,100).into(backgroundViewHolder.mStockImageView);
    }

    @Override
    public int getItemCount() {
        return mStockImagesList.size();
    }

    class BackgroundViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mStockImageView;

        BackgroundViewHolder(@NonNull View itemView) {
            super(itemView);
            mStockImageView = itemView.findViewById(R.id.stock_image_preview);
            mStockImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int drawable = mStockImagesList.get(getAdapterPosition());
            mOnStockImageSelectedListener.onStockImageSelected(drawable);
        }
    }
}
