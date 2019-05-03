package com.philosofy.nvn.philosofy.custom;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class ColorsItemDecoration extends RecyclerView.ItemDecoration {

    private static final int SPACE = 22;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childPosition = parent.getChildAdapterPosition(view);
        if (childPosition == 2 || childPosition == state.getItemCount() - 1) {
            outRect.right = SPACE;
        }
    }
}
