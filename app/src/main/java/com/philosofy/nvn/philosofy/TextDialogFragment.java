package com.philosofy.nvn.philosofy;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.philosofy.nvn.philosofy.database.AppDatabase;
import com.philosofy.nvn.philosofy.database.CrudExecutors;
import com.philosofy.nvn.philosofy.database.Quote;
import com.philosofy.nvn.philosofy.utils.Constants;

import java.util.Date;

public class TextDialogFragment extends DialogFragment {

    private ImageView mDoneImageView;
    private ImageView mCloseImageView;
    private CheckBox mSaveQuoteCheckBox;
    private TextInputEditText mAuthorEditText;
    private AutoCompleteTextView mCategoryTextView;

    private TextInputLayout mAuthorTextInputLayout;
    private TextInputLayout mCategoryTextInputLayout;

    private EditText mQuoteEditText;

    private String preText = "";

    private OnDoneTextEditListener mOnTextDoneListener;

    private InputMethodManager mInputMethodManager;

    public interface OnDoneTextEditListener {
        void onDoneTextEdit(String quoteText);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_add_text, container, false);
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

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initiateView(view);
        String[] categoriesArray = getResources().getStringArray(R.array.qod_categories_entries);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, categoriesArray);
        mCategoryTextView.setThreshold(0);
        mCategoryTextView.setAdapter(categoryAdapter);

        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        mDoneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String quoteText = mQuoteEditText.getText().toString().trim();

                if (areInputsValid()) {
                    mOnTextDoneListener.onDoneTextEdit(" " + quoteText + " ");
                    mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    TextDialogFragment.this.dismiss();
                    final String author = mAuthorEditText.getText().toString().trim();
                    final String category = mCategoryTextView.getText().toString().trim();
                    if (mSaveQuoteCheckBox.isChecked()) {
                        CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                AppDatabase db = AppDatabase.getInstance(getContext());
                                db.quotesDao().insertQuote(new Quote(quoteText, author, category, new Date(), Constants.QUOTE_USER));
                            }
                        });
                    }
                }
            }
        });

        mCloseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                TextDialogFragment.this.dismiss();
            }
        });

        mSaveQuoteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mAuthorTextInputLayout.setVisibility(View.INVISIBLE);
                    mCategoryTextInputLayout.setVisibility(View.INVISIBLE);
                } else {
                    mAuthorTextInputLayout.setVisibility(View.VISIBLE);
                    mCategoryTextInputLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        mQuoteEditText.setText(preText);
    }

    public void setOnTextDoneListener(OnDoneTextEditListener onTextDoneListener) {
        mOnTextDoneListener = onTextDoneListener;
    }

    public void setPreText(String quoteText) {
        preText = quoteText;
    }

    private void initiateView(View rootView) {
        mDoneImageView = rootView.findViewById(R.id.done_write_imageview);
        mCloseImageView = rootView.findViewById(R.id.close_write_imageview);
        mQuoteEditText = rootView.findViewById(R.id.quote_edittext);
        mAuthorEditText = rootView.findViewById(R.id.save_author_edittext);
        mCategoryTextView = rootView.findViewById(R.id.save_category_autotextview);
        mSaveQuoteCheckBox = rootView.findViewById(R.id.save_quote_checkbox);

        mAuthorTextInputLayout = rootView.findViewById(R.id.save_author_text_input_layout);
        mCategoryTextInputLayout = rootView.findViewById(R.id.save_category_text_input_layout);
    }

    private boolean areInputsValid() {
        String error = "This field is required";

        Editable quote = mQuoteEditText.getText();

        if (!mSaveQuoteCheckBox.isChecked()) {
            if (quote == null || TextUtils.isEmpty(quote.toString().trim())) {
                mQuoteEditText.setError(error);
                return false;
            } else {
                return true;
            }
        }

        if (TextUtils.isEmpty(quote.toString().trim())) {
            mQuoteEditText.setError(error);
            return false;
        }

        return true;
    }
}
