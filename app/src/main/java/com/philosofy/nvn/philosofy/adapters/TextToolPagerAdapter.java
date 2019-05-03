package com.philosofy.nvn.philosofy.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TextToolPagerAdapter extends FragmentPagerAdapter {

    private final int NO_OF_FRAGMENTS = 2;

    private List<Fragment> mFragments = new ArrayList<>();

    public TextToolPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return mFragments.get(0);
        } else {
            return mFragments.get(1);
        }
    }

    @Override
    public int getCount() {
        return NO_OF_FRAGMENTS;
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }
}
