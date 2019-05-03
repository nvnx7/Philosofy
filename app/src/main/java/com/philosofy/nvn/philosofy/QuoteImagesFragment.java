package com.philosofy.nvn.philosofy;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.philosofy.nvn.philosofy.adapters.QuoteImagesAdapter;
import com.philosofy.nvn.philosofy.utils.Constants;
import com.philosofy.nvn.philosofy.utils.StorageUtils;

import java.io.File;
import java.io.FileFilter;

public class QuoteImagesFragment extends Fragment
        implements QuotesActivity.QuoteImagesCallback {

    private RecyclerView mQuoteImagesRecyclerView;
    private LinearLayout mQuoteImagesEmptyLayout;
    private QuoteImagesAdapter quoteImagesAdapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreateView called");
        View rootView = inflater.inflate(R.layout.fragment_quotes_designed, container, false);

        mQuoteImagesRecyclerView = rootView.findViewById(R.id.designed_quotes_rv);
        mQuoteImagesEmptyLayout = rootView.findViewById(R.id.designed_quotes_empty_layout);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onViewCreated called");
        super.onViewCreated(view, savedInstanceState);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mQuoteImagesRecyclerView.setLayoutManager(layoutManager);
        quoteImagesAdapter = new QuoteImagesAdapter(getContext(),
                (QuoteImagesAdapter.OnQuoteImageClickedListener) getContext(), null);
        mQuoteImagesRecyclerView.setAdapter(quoteImagesAdapter);

        if (!StorageUtils.isStoragePermissionGranted(getContext())) {
            return;
        }

        File quotesDirectory = StorageUtils.getStoragePath(Constants.IMAGE_EDITED);
        File[] files = quotesDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.getPath().endsWith(".png"));
            }
        });

        if (files != null && files.length > 0) {
            mQuoteImagesEmptyLayout.setVisibility(View.INVISIBLE);
            quoteImagesAdapter.swapData(files);
        } else {
            mQuoteImagesEmptyLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onQuoteImagesTabSelected(Context context) {
        if (!StorageUtils.isStoragePermissionGranted(context)) {
            StorageUtils.tryRequestStoragePermissionForSaving(context);
        }
    }

    @Override
    public void onStoragePermissionGranted() {
        File quotesDirectory = StorageUtils.getStoragePath(Constants.IMAGE_EDITED);
        File[] files = quotesDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.getPath().endsWith(".png"));
            }
        });

        if (files != null && files.length != 0) {
            mQuoteImagesEmptyLayout.setVisibility(View.INVISIBLE);
            quoteImagesAdapter.swapData(files);
        } else {
            mQuoteImagesEmptyLayout.setVisibility(View.VISIBLE);
        }
    }
}
