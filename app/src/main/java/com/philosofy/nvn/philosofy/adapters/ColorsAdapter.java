package com.philosofy.nvn.philosofy.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philosofy.nvn.philosofy.R;
import com.philosofy.nvn.philosofy.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ColorViewHolder> {

    private List<Integer> mColorsList;
    private OnColorSelectedListener mOnColorSelectedListener;
    private Context mContext;

    public ColorsAdapter(Context context, OnColorSelectedListener onColorSelectedListener) {
        mContext = context;
        mOnColorSelectedListener = onColorSelectedListener;

        int[] colorsArray = context.getResources().getIntArray(R.array.material_colors);
        mColorsList = PreferencesUtils.getRecentColors(context);

        for (int color : colorsArray) {
            mColorsList.add(color);
        }
    }

    public interface OnColorSelectedListener {
        void onTextColorSelected(int colorCode);
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemview_colors, viewGroup, false);

        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder viewHolder, int i) {
        viewHolder.mColorImageView.getBackground().setColorFilter(mColorsList.get(i), PorterDuff.Mode.SRC_OVER);
    }

    @Override
    public int getItemCount() {
        if (mColorsList == null) {
            return 0;
        }
        return mColorsList.size();
    }

    public void updateRecentColors() {
        ArrayList<Integer> recentColors = PreferencesUtils.getRecentColors(mContext);
        mColorsList.set(0, recentColors.get(0));
        mColorsList.set(1, recentColors.get(1));
        mColorsList.set(2, recentColors.get(2));
        notifyItemRangeChanged(0,3);
    }

    class ColorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mColorImageView;

        ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            mColorImageView = itemView.findViewById(R.id.color_preview_imageview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnColorSelectedListener.onTextColorSelected(mColorsList.get(getAdapterPosition()));
        }
    }
}
