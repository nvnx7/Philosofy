package com.philosofy.nvn.philosofy.adapters;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.philosofy.nvn.philosofy.QuoteImagesFragment;
import com.philosofy.nvn.philosofy.QuotesFragment;
import com.philosofy.nvn.philosofy.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class QuotesPagerAdapter extends FragmentPagerAdapter {

    private final int NUM_PAGES = 3;
    List<Fragment> fragments;

    public QuotesPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        if (i == 0) {
            bundle.putString(Constants.KEY_BUNDLE_QUOTE, Constants.QUOTE_DAILY_QODS);
            //QuotesFragment quotesFragment = new QuotesFragment();
            QuotesFragment quotesFragment = (QuotesFragment) fragments.get(i);
            quotesFragment.setArguments(bundle);
            return quotesFragment;
        } else if (i == 1) {
            bundle.putString(Constants.KEY_BUNDLE_QUOTE, Constants.QUOTE_USER);
            //QuotesFragment quotesFragment = new QuotesFragment();
            QuotesFragment quotesFragment = (QuotesFragment) fragments.get(i);
            quotesFragment.setArguments(bundle);
            return quotesFragment;
        } else {
            QuoteImagesFragment quoteImagesFragment = (QuoteImagesFragment) fragments.get(i);
            return quoteImagesFragment;
        }

    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "QUOTE OF THE DAY";
        } else if (position == 1) {
            return "YOUR QUOTES";
        } else {
            return "DESIGNED QUOTES";
        }
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }
}
