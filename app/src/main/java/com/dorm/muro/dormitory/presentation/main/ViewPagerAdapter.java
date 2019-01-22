package com.dorm.muro.dormitory.presentation.main;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> pages;
    private ArrayList<String> titles;
    private final int TOTAL_PAGES = 4;

    ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        pages = new ArrayList<>(TOTAL_PAGES);
        titles = new ArrayList<>(TOTAL_PAGES);
    }

    void addFragment(int position, Fragment fragment, String titleRes) {
        pages.add(position, fragment);
        titles.add(position, titleRes);
    }

    @Override
    public Fragment getItem(int i) {
        return pages.get(i);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return TOTAL_PAGES;
    }
}
