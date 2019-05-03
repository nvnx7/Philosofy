package com.philosofy.nvn.philosofy.adapters;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.philosofy.nvn.philosofy.LanguageFontFragment;
import com.philosofy.nvn.philosofy.utils.Constants;

public class LanguagesFontsPagerAdapter extends FragmentPagerAdapter {

    private final int NUM_PAGES = 8;

    public LanguagesFontsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        switch (i) {
            case 0:
                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_LATIN);
                break;
            case 1:
                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_DEVANAGARI);
                break;
            case 2:
                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_ARABIC);
                break;
            case 3:
                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_CYRILLIC);
                break;
//            case 4:
//                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_JAPANESE);
//                break;
            case 4:
                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_KOREAN);
                break;
//            case 6:
//                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_CHINESE);
//                break;
            case 5:
                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_TAMIL);
                break;
            case 6:
                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_TELUGU);
                break;
            case 7:
                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_BENGALI);
                break;
            default:
                bundle.putString(Constants.KEY_BUNDLE_LANGUAGE, Constants.LANGUAGE_LATIN);
                break;
        }

        LanguageFontFragment languageFontFragment = new LanguageFontFragment();
        languageFontFragment.setArguments(bundle);
        return  languageFontFragment;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "LATIN";
            case 1:
                return "DEVANAGARI";
            case 2:
                return "ARABIC";
            case 3:
                return "CYRILLIC";
//            case 4:
//                return "JAPANESE";
            case 4:
                return "KOREAN";
//            case 6:
//                return "CHINESE";
            case 5:
                return "TAMIL";
            case 6:
                return "TELUGU";
            case 7:
                return "BENGALI";
            default:
                return "LATIN";
        }
    }
}
