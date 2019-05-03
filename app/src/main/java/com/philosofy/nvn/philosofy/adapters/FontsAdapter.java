package com.philosofy.nvn.philosofy.adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.core.provider.FontsContractCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.R;
import com.philosofy.nvn.philosofy.database.FavoriteFont;
import com.philosofy.nvn.philosofy.utils.FontUtils;

import java.util.ArrayList;

public class FontsAdapter extends RecyclerView.Adapter<FontsAdapter.FontsViewHolder> {

    private ArrayList<FavoriteFont> mFavoriteFontList;

    private Context mContext;
    private FavoriteFontsCallback mFavoriteFontsCallback;


    public FontsAdapter(Context context, FavoriteFontsCallback favoriteFontsCallback,
                        ArrayList<FavoriteFont> favoriteFontsList) {
        mContext = context;
        mFavoriteFontsCallback = favoriteFontsCallback;

        mFavoriteFontList = favoriteFontsList;
    }

    public interface FavoriteFontsCallback {
        void onFontSelected(Typeface typeface);
        void onFontRemove(FavoriteFont favoriteFont);
    }

    @NonNull
    @Override
    public FontsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemview_fonts, viewGroup, false);

        return new FontsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FontsViewHolder fontsViewHolder, int i) {

        final String shortPreviewString
                = FontUtils.getShortFontPreviewString(mContext, mFavoriteFontList.get(i).getLang());

        final String fontName = mFavoriteFontList.get(i).getFontName();

        FontsContractCompat.FontRequestCallback fontRequestCallback
                = new FontsContractCompat.FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                fontsViewHolder.mFontPreviewTextView.setText(shortPreviewString);
                fontsViewHolder.mFontPreviewTextView.setTypeface(typeface);
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                Log.i(getClass().getSimpleName(), "REQUEST FAILED FOR FONT: " + fontName);
            }
        };

        FontUtils.requestFont(mContext, fontName ,fontRequestCallback);
    }

    @Override
    public int getItemCount() {
        if (mFavoriteFontList == null) {
            return 0;
        }
        return mFavoriteFontList.size();
    }

    public void swapData(ArrayList<FavoriteFont> favoriteFonts) {
        if (mFavoriteFontList != null) {
            mFavoriteFontList = null;
        }

        mFavoriteFontList = favoriteFonts;
        notifyDataSetChanged();
    }

    class FontsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView mFontPreviewTextView;

        FontsViewHolder(View itemView) {
            super(itemView);
            mFontPreviewTextView = itemView.findViewById(R.id.font_preview_textview);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mFavoriteFontsCallback.onFontSelected(mFontPreviewTextView.getTypeface());
        }

        @Override
        public boolean onLongClick(View v) {
            FavoriteFont favoriteFont = mFavoriteFontList.get(getAdapterPosition());
            mFavoriteFontsCallback.onFontRemove(favoriteFont);
            return true;
        }
    }
}
