package com.philosofy.nvn.philosofy.custom;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class ThumbnailItemDecoration extends RecyclerView.ItemDecoration {

    private static final int SPACE = 12;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            outRect.right = 0;
        } else {
            outRect.right = SPACE;
            outRect.left = 0;
        }
    }
}
