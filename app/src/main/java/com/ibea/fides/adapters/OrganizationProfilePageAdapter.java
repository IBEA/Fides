package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ibea.fides.ui.OrganizationManagmentShiftFragment;
import com.ibea.fides.ui.OrganizationPastShiftFragment;
import com.ibea.fides.ui.OrganizationProfileFragment;

/**
 * Created by N8Home on 1/30/17.
 */

public class OrganizationProfilePageAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    // Tab Titles
    private String tabtitles[] = new String[] { "Profile", "Manage Shift", "Past Shift", "Past User" };
    Context context;

    public OrganizationProfilePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open FragmentTab1.java
            case 0:
                OrganizationProfileFragment organizationProfileFragment = new OrganizationProfileFragment();
                return organizationProfileFragment;

            // Open FragmentTab2.java
            case 1:
                OrganizationManagmentShiftFragment organizationManagmentShiftFragment = new OrganizationManagmentShiftFragment();
                return organizationManagmentShiftFragment;

            // Open FragmentTab3.java
            case 2:
               OrganizationPastShiftFragment organizationPastShiftFragment = new OrganizationPastShiftFragment();
                return organizationPastShiftFragment;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
