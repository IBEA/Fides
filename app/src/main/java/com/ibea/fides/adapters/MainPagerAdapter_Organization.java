package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ibea.fides.ui._TOGO_OrganizationPastUserFragment;
import com.ibea.fides.ui.ProfileForOrganizationFragment;
import com.ibea.fides.ui.ShiftsCompletedForOrganizationFragment;
import com.ibea.fides.ui.ShiftsPendingForOrganizationFragment;

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

            // Open  ProfileForOrganizationFragment.java
            case 0:
                ProfileForOrganizationFragment profileForOrganizationFragment = new ProfileForOrganizationFragment();
                return profileForOrganizationFragment;

            // Open _TOGO_OrganizationManageShiftsFragment.java
            case 1:
                ShiftsPendingForOrganizationFragment pendingShiftsFragment = new ShiftsPendingForOrganizationFragment();
                return pendingShiftsFragment;

            // Open ShiftsCompletedForOrganizationFragment.java
            case 2:
               ShiftsCompletedForOrganizationFragment completedShiftsFragment = new ShiftsCompletedForOrganizationFragment();
                return completedShiftsFragment;

            // Open _TOGO_OrganizationPastShiftFragment.java
            case 3:
                _TOGO_OrganizationPastUserFragment organizationPastUserFragment = new _TOGO_OrganizationPastUserFragment();
                return organizationPastUserFragment;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
