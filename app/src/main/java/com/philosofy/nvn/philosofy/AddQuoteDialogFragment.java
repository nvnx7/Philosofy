package com.philosofy.nvn.philosofy;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.database.Quote;
import com.philosofy.nvn.philosofy.utils.Constants;

import java.util.Date;

public class AddQuoteDialogFragment extends DialogFragment {

    private TextInputEditText mAuthorTextInputEditText;
    private AutoCompleteTextView mCategoryAutoCompleteTextView;
    private TextInputEditText mQuoteEditText;
    private TextView mAddQuoteTextView;
    private TextView mCancelAddingTextView;

    private OnAddQuoteListener mOnAddQuoteListener;

    public interface OnAddQuoteListener{
        void onAddQuote(Quote quote);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            int w = (int) Math.round(0.95*width);
            int h = (int) Math.round(0.95*height);

            dialog.getWindow().setLayout(w, h);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_add_quote, container, false);

        mAuthorTextInputEditText = rootView.findViewById(R.id.author_name_edittext);
        mCategoryAutoCompleteTextView = rootView.findViewById(R.id.category_autocomplete_textview);
        mQuoteEditText = rootView.findViewById(R.id.quote_edit_text);
        mAddQuoteTextView = rootView.findViewById(R.id.add_quote_textview);
        mCancelAddingTextView = rootView.findViewById(R.id.cancel_quote_textview);

        String[] categoriesArray = getResources().getStringArray(R.array.qod_categories_entries);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, categoriesArray);
        mCategoryAutoCompleteTextView.setThreshold(0);
        mCategoryAutoCompleteTextView.setAdapter(categoryAdapter);

        mAddQuoteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!areInputsValid()) {
                    return;
                }

                String author = mAuthorTextInputEditText.getText().toString().trim();
                String category = mCategoryAutoCompleteTextView.getText().toString().trim();
                String quote = mQuoteEditText.getText().toString().trim();
                Quote userQuote = new Quote(quote, author, category, new Date(), Constants.QUOTE_USER);
                mOnAddQuoteListener.onAddQuote(userQuote);
                AddQuoteDialogFragment.this.dismiss();
            }
        });

        mCancelAddingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddQuoteDialogFragment.this.dismiss();
            }
        });

        return rootView;
    }

    public void setOnAddQuoteListener(OnAddQuoteListener onAddQuoteListener) {
        mOnAddQuoteListener = onAddQuoteListener;
    }

    private boolean areInputsValid() {

        Editable quote = mQuoteEditText.getText();

        if (quote == null || TextUtils.isEmpty(quote.toString().trim())) {
            mQuoteEditText.setError("This field is required");
            return false;
        }

        return true;
    }
}
