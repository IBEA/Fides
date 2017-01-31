package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.ibea.fides.ui.ProfileTab;
import com.ibea.fides.ui.ShiftDetailsFragment;
import com.ibea.fides.ui.ShiftsSearchFragment;
import com.ibea.fides.ui.hoursFragment;


/**
 * Created by Nhat on 1/27/17.
 */

// This Page Adapter is handling fragment for the profile and shift tab.

public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;
    // Tab Titles
    private String tabtitles[] = new String[] { "Profile", "Shifts", "Find" , "History" };
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
                ShiftDetailsFragment organizationshiftfragment = new ShiftDetailsFragment();
                return organizationshiftfragment;

             //Open FragmentTab3.java
            case 2:
                ShiftsSearchFragment searchAsUserFragment = new ShiftsSearchFragment();
                return searchAsUserFragment;

            case 3:
                hoursFragment fragmenttab2 = new hoursFragment();
                return fragmenttab2;


        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}