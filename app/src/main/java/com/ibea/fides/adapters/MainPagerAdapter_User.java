package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ibea.fides.ui.ProfileForUsersActivity;
import com.ibea.fides.ui.ShiftsCompletedForVolunteersFragment;
import com.ibea.fides.ui.ShiftsPendingForVolunteerFragment;
import com.ibea.fides.ui.ShiftsSearchFragment;


/**
 * Created by Nhat on 1/27/17.
 */

// This Page Adapter is handling fragment for the profile and shift tab.

public class MainPagerAdapter_User extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;
    // Tab Titles
    private String tabtitles[] = new String[] { "Profile", "Shifts", "Find" , "History" };
    Context context;

    public MainPagerAdapter_User(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open User Profile fragment
            case 0:
                ProfileForUsersActivity userProfileFragment = new ProfileForUsersActivity();
                return userProfileFragment;

            // Open List of Upcoming Shifts fragment
            case 1:
                ShiftsPendingForVolunteerFragment upcomingUserShiftsFragment = new ShiftsPendingForVolunteerFragment();
                return upcomingUserShiftsFragment;

             //Open Search for Shifts fragment
            case 2:
                ShiftsSearchFragment shiftSearchFragment = new ShiftsSearchFragment();
                return shiftSearchFragment;
            // Open User's Shift History fragment
            case 3:
                ShiftsCompletedForVolunteersFragment userShiftHistoryFragment = new ShiftsCompletedForVolunteersFragment();
                return userShiftHistoryFragment;


        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}