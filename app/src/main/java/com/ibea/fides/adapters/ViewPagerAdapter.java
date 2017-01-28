package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.ibea.fides.ui.ProfileTab;
import com.ibea.fides.ui.ShiftFragment;
import com.ibea.fides.ui.hoursFragment;


/**
 * Created by Nhat on 1/27/17.
 */

// This Page Adapter is handling fragment for the profile and shift tab.

public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    // Tab Titles
    private String tabtitles[] = new String[] { "Profile", "Shift", "Hours" };
    Context context;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open ProfileTab.java
            case 0:
                ProfileTab fragmenttab = new ProfileTab();
                return fragmenttab;

            // Open ShiftFragment.java
            case 1:
                ShiftFragment organizationshiftfragment = new ShiftFragment();
                return organizationshiftfragment;

             //Open FragmentTab3.java
            case 2:
                hoursFragment fragmenttab3 = new hoursFragment();
                return fragmenttab3;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}