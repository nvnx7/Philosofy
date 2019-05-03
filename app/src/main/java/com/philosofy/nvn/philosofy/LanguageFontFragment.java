package com.philosofy.nvn.philosofy;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philosofy.nvn.philosofy.adapters.DownloadableFontsAdapter;
import com.philosofy.nvn.philosofy.utils.Constants;
import com.philosofy.nvn.philosofy.utils.FontUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class LanguageFontFragment extends Fragment {
    private static final String TAG = LanguageFontFragment.class.getSimpleName();

    private RecyclerView mDownloadableFontsRecyclerView;
    private DownloadableFontsAdapter mDownloadableFontsAdapter;

    private String[] fontFamilyNames;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_language_fonts, container, false);

        mDownloadableFontsRecyclerView = rootView.findViewById(R.id.downloadable_fonts_rv);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mDownloadableFontsRecyclerView.setLayoutManager(layoutManager);

        String language = (String) getArguments().get(Constants.KEY_BUNDLE_LANGUAGE);

        fontFamilyNames = FontUtils.getFontFamilyNamesByLanguage(getContext(), language);
        ArrayList<String> fontNames = new ArrayList<>(Arrays.asList(fontFamilyNames));

        mDownloadableFontsAdapter = new DownloadableFontsAdapter(getContext(), fontNames, language);
        mDownloadableFontsAdapter.setDownloadableFontsCallback((DownloadableFontsAdapter.DownloadableFontsCallback) getContext());
        mDownloadableFontsRecyclerView.setAdapter(mDownloadableFontsAdapter);

        return rootView;
    }
}
