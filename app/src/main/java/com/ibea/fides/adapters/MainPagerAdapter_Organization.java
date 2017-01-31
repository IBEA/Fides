package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ibea.fides.ui.OrganizationManageShiftsFragment;
import com.ibea.fides.ui.OrganizationPastShiftFragment;
import com.ibea.fides.ui.OrganizationPastUserFragment;
import com.ibea.fides.ui.OrganizationProfileFragment;

/**
 * Created by N8Home on 1/30/17.
 */

public class MainPagerAdapter_Organization extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;
    // Tab Titles
    private String tabtitles[] = new String[] { "Profile", "Manage Shift", "Past Shift", "Past User" };
    Context context;

    public MainPagerAdapter_Organization(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open  OrganizationProfileFragment.java
            case 0:
                OrganizationProfileFragment organizationProfileFragment = new OrganizationProfileFragment();
                return organizationProfileFragment;

            // Open OrganizationManageShiftsFragment.java
            case 1:
                OrganizationManageShiftsFragment organizationManageShiftsFragment = new OrganizationManageShiftsFragment();
                return organizationManageShiftsFragment;

            // Open OrganizationPastShiftFragment.java
            case 2:
               OrganizationPastShiftFragment organizationPastShiftFragment = new OrganizationPastShiftFragment();
                return organizationPastShiftFragment;

            // Open OrganizationPastShiftFragment.java
            case 3:
                OrganizationPastUserFragment organizationPastUserFragment = new OrganizationPastUserFragment();
                return organizationPastUserFragment;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
