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

public class MainPagerAdapter_User extends FragmentPagerAdapter {

    int pageCount;
    // Tab Titles
    private ArrayList<String> tabtitles;
    private ArrayList<Fragment> fragmentList;

    public MainPagerAdapter_User(FragmentManager fm, int _pageCount, ArrayList<String> _tabTitles, ArrayList<Fragment> _fragmentList) {
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
//        switch (position) {
//
//            // Open User Profile fragment
//            case 0:
//                ProfileForVolunteerFragment userProfileFragment = new ProfileForVolunteerFragment();
//                return userProfileFragment;
//
//            // Open List of Upcoming Shifts fragment
//            case 1:
//                ShiftsPendingForVolunteerFragment upcomingUserShiftsFragment = new ShiftsPendingForVolunteerFragment();
//                return upcomingUserShiftsFragment;
//
//             //Open Search for Shifts fragment
//            case 2:
//                ShiftSearchFragment shiftSearchFragment = new ShiftSearchFragment();
//                return shiftSearchFragment;
//            // Open User's Shift History fragment
//            case 3:
//                ShiftsCompletedForVolunteerFragment userShiftHistoryFragment = new ShiftsCompletedForVolunteerFragment();
//                return userShiftHistoryFragment;
//
//
//        }
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles.get(position);
    }
}