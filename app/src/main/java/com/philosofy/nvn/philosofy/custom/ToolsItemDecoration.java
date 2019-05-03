package com.philosofy.nvn.philosofy.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

public class ToolsItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private static final int NO_OF_ITEMS = 5;

    public ToolsItemDecoration(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        int availableSpace = displayMetrics.widthPixels - (NO_OF_ITEMS*convertDpToPixel(36, activity)) - convertDpToPixel(16, activity);
        space = Math.round((float) availableSpace/ (float) NO_OF_ITEMS);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = 48;
            outRect.right = space;
        } else if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            outRect.left = 0;
            outRect.right = 48;
        } else {
            outRect.left = 0;
            outRect.right = space;
        }

    }

    private static int convertDpToPixel(float dp, Context context){
        return Math.round(dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
