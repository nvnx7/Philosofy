package com.philosofy.nvn.philosofy.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.R;
import com.philosofy.nvn.philosofy.database.Quote;

import java.util.ArrayList;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuotesViewHolder> {

    private static final String TAG = QuotesAdapter.class.getSimpleName();

    private ArrayList<Quote> mQuotesList;

    private QuotesCallback mQuotesCallback;

    public QuotesAdapter(ArrayList<Quote> quotesList, QuotesCallback quotesCallback) {
        mQuotesList = quotesList;
        mQuotesCallback = quotesCallback;
    }

    public interface QuotesCallback{
        void onEditQuote(String quote);
        void onShareQuote(String quote);
        void onSearchAuthor(String author);
        void onDeleteQuote(Quote quote);
        void onCopyQuote(String quote);
    }

    @NonNull
    @Override
    public QuotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemview_quotes, viewGroup, false);

        return new QuotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuotesViewHolder quotesViewHolder, int i) {
        String category = mQuotesList.get(i).getCategory();
        String quote = mQuotesList.get(i).getQuote();
        String author = mQuotesList.get(i).getAuthor();

        quotesViewHolder.mCategoryTextView.setText(category.toUpperCase());
        quotesViewHolder.mQuoteTextView.setText(quote);
        quotesViewHolder.mAuthorTextView.setText(author);
    }

    @Override
    public int getItemCount() {
        if (mQuotesList == null) {
            return 0;
        }
        return mQuotesList.size();
    }

    public void swapData(ArrayList<Quote> quotes) {
        if (mQuotesList != null) {
            mQuotesList = null;
        }
        mQuotesList = quotes;
        notifyDataSetChanged();
    }

    class QuotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mCategoryTextView;
        private TextView mQuoteTextView;
        private TextView mAuthorTextView;
        private ImageView mShareQuoteImageView;
        private ImageView mCopyQuoteImageView;
        private ImageView mEditQuoteImageView;
        private ImageView mSearchWikiImageView;
        private ImageView mDeleteQuoteImageView;

        QuotesViewHolder(@NonNull View itemView) {
            super(itemView);
            mCategoryTextView = itemView.findViewById(R.id.category_textview);
            mQuoteTextView = itemView.findViewById(R.id.qod_textview);
            mAuthorTextView = itemView.findViewById(R.id.author_textview);

            mShareQuoteImageView = itemView.findViewById(R.id.share_quote_imageview);
            mCopyQuoteImageView = itemView.findViewById(R.id.copy_quote_imageview);
            mEditQuoteImageView = itemView.findViewById(R.id.edit_quote_imageview);
            mSearchWikiImageView = itemView.findViewById(R.id.search_wiki_imageview);
            mDeleteQuoteImageView = itemView.findViewById(R.id.delete_quote_imageview);

            mShareQuoteImageView.setOnClickListener(this);
            mEditQuoteImageView.setOnClickListener(this);
            mSearchWikiImageView.setOnClickListener(this);
            mDeleteQuoteImageView.setOnClickListener(this);
            mCopyQuoteImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Quote quote = mQuotesList.get(getAdapterPosition());

            switch (v.getId()) {
                case R.id.share_quote_imageview:
                    mQuotesCallback.onShareQuote(quote.getQuote());
                    break;
                case R.id.edit_quote_imageview:
                    mQuotesCallback.onEditQuote(quote.getQuote());
                    break;
                case R.id.search_wiki_imageview:
                    mQuotesCallback.onSearchAuthor(quote.getAuthor());
                    break;
                case R.id.delete_quote_imageview:
                    mQuotesCallback.onDeleteQuote(quote);
                    break;
                case R.id.copy_quote_imageview:
                    mQuotesCallback.onCopyQuote(quote.getQuote());
                    break;
            }
        }
    }
}
