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
import android.widget.ImageView;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.R;
import com.philosofy.nvn.philosofy.database.AppDatabase;
import com.philosofy.nvn.philosofy.database.CrudExecutors;
import com.philosofy.nvn.philosofy.utils.Constants;
import com.philosofy.nvn.philosofy.utils.FontUtils;

import java.util.ArrayList;
import java.util.List;

public class DownloadableFontsAdapter extends RecyclerView.Adapter<DownloadableFontsAdapter.DownloadableFontsViewHolder> {

    private static final String TAG = DownloadableFontsAdapter.class.getSimpleName();

    private ArrayList<String> mFontNames;

    private Context mContext;
    private String mLanguage;

    private DownloadableFontsCallback mDownloadableFontsCallback;

    private List<String> currentFavoriteFonts;

    public DownloadableFontsAdapter(Context context, ArrayList<String> fontNames, String lang) {
        mFontNames = fontNames;
        mContext = context;
        mLanguage = lang;

        CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(mContext);
                currentFavoriteFonts = db.favoriteFontDao().getFavoriteFontNamesByLang(mLanguage);
            }
        });
    }

    public interface DownloadableFontsCallback {
        void onFontAddedToFavorites(String fontFamilyName, String lang, int favoriteTag);
        void onWriteWithFont(String fontFamilyName);
    }

    @NonNull
    @Override
    public DownloadableFontsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemview_downloadable_fonts, viewGroup, false);

        String previewString = FontUtils.getFontPreviewString(mContext, mLanguage);
        TextView previewTextView = itemView.findViewById(R.id.downloadable_font_preview);
        previewTextView.setText(previewString);

        return new DownloadableFontsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DownloadableFontsViewHolder downloadableFontsViewHolder, int i) {

        downloadableFontsViewHolder.mLoadingIndicatorTextView.setVisibility(View.VISIBLE);
        downloadableFontsViewHolder.mDownloadableFontPreviewTextView.setVisibility(View.INVISIBLE);

        final int position = i;

        FontsContractCompat.FontRequestCallback fontRequestCallback
                = new FontsContractCompat.FontRequestCallback() {

            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                downloadableFontsViewHolder.mDownloadableFontPreviewTextView.setTypeface(typeface);
                downloadableFontsViewHolder.mLoadingIndicatorTextView.setVisibility(View.INVISIBLE);
                downloadableFontsViewHolder.mDownloadableFontPreviewTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                try {
                    mFontNames.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mFontNames.size());
                } catch (IndexOutOfBoundsException e) {
                    Log.i(TAG, "exception occurred");
                }
            }
        };

        String fontName = mFontNames.get(i);

        if (currentFavoriteFonts.contains(fontName)) {
            downloadableFontsViewHolder.mFavoriteFontImageView.setImageResource(R.drawable.ic_favorite);
            downloadableFontsViewHolder.mFavoriteFontImageView.setTag(Constants.FAVORITE);
        } else {
            downloadableFontsViewHolder.mFavoriteFontImageView.setImageResource(R.drawable.ic_not_favorite);
            downloadableFontsViewHolder.mFavoriteFontImageView.setTag(Constants.NOT_FAVORITE);
        }

        downloadableFontsViewHolder.mDownloadableFontNameTextView.setText(fontName);
        FontUtils.requestFont(mContext, fontName, fontRequestCallback);
    }

    @Override
    public int getItemCount() {
        if (mFontNames == null) {
            return 0;
        }
        return mFontNames.size();
    }

    public void setDownloadableFontsCallback(DownloadableFontsCallback downloadableFontsCallback) {
        mDownloadableFontsCallback = downloadableFontsCallback;
    }

    class DownloadableFontsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mDownloadableFontPreviewTextView;
        private TextView mDownloadableFontNameTextView;
        private TextView mLoadingIndicatorTextView;
        private ImageView mFavoriteFontImageView;
        private ImageView mWriteWithFontImageView;

        DownloadableFontsViewHolder(@NonNull View itemView) {
            super(itemView);
            mDownloadableFontPreviewTextView = itemView.findViewById(R.id.downloadable_font_preview);
            mDownloadableFontNameTextView = itemView.findViewById(R.id.font_name_textview);
            mFavoriteFontImageView = itemView.findViewById(R.id.favorite_font_imageview);
            mWriteWithFontImageView = itemView.findViewById(R.id.write_with_font_imageview);
            mLoadingIndicatorTextView = itemView.findViewById(R.id.loading_indicator_textview);

            mDownloadableFontPreviewTextView.setOnClickListener(this);
            mFavoriteFontImageView.setOnClickListener(this);
            mWriteWithFontImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            String fontName = mDownloadableFontNameTextView.getText().toString();
            int tag = (int) mFavoriteFontImageView.getTag();

            if (id == R.id.favorite_font_imageview) {
                switchImageResource(tag);
                mDownloadableFontsCallback.onFontAddedToFavorites(fontName, mLanguage, tag);
            } else if (id == R.id.write_with_font_imageview || id == R.id.downloadable_font_preview) {
                mDownloadableFontsCallback.onWriteWithFont(fontName);
            }
        }

        private void switchImageResource(int tag) {
            if (tag == Constants.FAVORITE) {
                mFavoriteFontImageView.setImageResource(R.drawable.ic_not_favorite);
                mFavoriteFontImageView.setTag(Constants.NOT_FAVORITE);
            } else if (tag == Constants.NOT_FAVORITE) {
                mFavoriteFontImageView.setImageResource(R.drawable.ic_favorite);
                mFavoriteFontImageView.setTag(Constants.FAVORITE);
            }
        }

    }
}
