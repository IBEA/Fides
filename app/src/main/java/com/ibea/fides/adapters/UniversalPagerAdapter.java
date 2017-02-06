package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ibea.fides.ui.ProfileForVolunteerFragment;
import com.ibea.fides.ui.ShiftSearchFragment;
import com.ibea.fides.ui.ShiftsCompletedForVolunteerFragment;
import com.ibea.fides.ui.ShiftsPendingForVolunteerFragment;

import java.util.ArrayList;


/**
 * Created by Nhat on 1/27/17.
 */

// This Page Adapter is handling fragment for the profile and shift tab.

public class UniversalPagerAdapter extends FragmentPagerAdapter {

    private int pageCount;
    // Tab Titles
    private ArrayList<String> tabtitles;
    private ArrayList<Fragment> fragmentList;

    public UniversalPagerAdapter(FragmentManager fm, int _pageCount, ArrayList<String> _tabTitles, ArrayList<Fragment> _fragmentList) {
        super(fm);

        tabtitles = _tabTitles;
        pageCount = _pageCount;
        fragmentList = _fragmentList;
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles.get(position);
    }
}