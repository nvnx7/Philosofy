package com.philosofy.nvn.philosofy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.adapters.QuotesAdapter;
import com.philosofy.nvn.philosofy.database.Quote;
import com.philosofy.nvn.philosofy.database.QuotesViewModel;
import com.philosofy.nvn.philosofy.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class QuotesFragment extends Fragment {

    private RecyclerView mQuotesRecyclerView;
    private QuotesAdapter quotesAdapter;
    private LinearLayout mQuotesEmptyLayout;
    private TextView mQuotesEmptyTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quotes, container, false);

        mQuotesRecyclerView = rootView.findViewById(R.id.quotes_rv);
        mQuotesEmptyLayout = rootView.findViewById(R.id.quotes_empty_layout);
        mQuotesEmptyTextView = rootView.findViewById(R.id.quotes_empty_text);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        quotesAdapter = new QuotesAdapter(null, (QuotesAdapter.QuotesCallback) getContext());
        mQuotesRecyclerView.setLayoutManager(layoutManager);
        mQuotesRecyclerView.setAdapter(quotesAdapter);

        String quoteType = getArguments().getString(Constants.KEY_BUNDLE_QUOTE);
        setUpQuotesData(quoteType);

        return rootView;
    }

    private void setUpQuotesData(String quoteType) {
        QuotesViewModel quotesViewModel = ViewModelProviders.of(this).get(QuotesViewModel.class);

        if (quoteType.equals(Constants.QUOTE_DAILY_QODS)) {
            LiveData<List<Quote>> qodsLiveData = quotesViewModel.getQodsLiveData();
            qodsLiveData.observe(this, new Observer<List<Quote>>() {
                @Override
                public void onChanged(@Nullable List<Quote> quotes) {
                    if (quotes == null || quotes.isEmpty()) {
                        mQuotesRecyclerView.setVisibility(View.INVISIBLE);
                        mQuotesEmptyLayout.setVisibility(View.VISIBLE);
                        mQuotesEmptyTextView.setText(getString(R.string.empty_message_qod));
                    } else {
                        mQuotesRecyclerView.setVisibility(View.VISIBLE);
                        mQuotesEmptyLayout.setVisibility(View.INVISIBLE);
                        quotesAdapter.swapData((ArrayList<Quote>) quotes);
                    }
                }
            });
        } else {
            LiveData<List<Quote>> userQuotesLiveData = quotesViewModel.getUserQuotesLiveData();
            userQuotesLiveData.observe(this, new Observer<List<Quote>>() {
                @Override
                public void onChanged(@Nullable List<Quote> quotes) {
                    if (quotes == null || quotes.isEmpty()) {
                        mQuotesRecyclerView.setVisibility(View.INVISIBLE);
                        mQuotesEmptyLayout.setVisibility(View.VISIBLE);
                        mQuotesEmptyTextView.setText(getString(R.string.empty_message_user_quote));
                    } else {
                        mQuotesRecyclerView.setVisibility(View.VISIBLE);
                        mQuotesEmptyLayout.setVisibility(View.INVISIBLE);
                        quotesAdapter.swapData((ArrayList<Quote>) quotes);
                    }
                }
            });
        }
    }
}
