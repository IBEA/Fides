package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ibea.fides.ui.FragmentTab1;
import com.ibea.fides.ui.FragmentTab2;

/**
 * Created by N8Home on 1/27/17.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    // Tab Titles
    private String tabtitles[] = new String[] { "Profile", "Shift" };
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

            // Open FragmentTab1.java
            case 0:
                FragmentTab1 fragmenttab1 = new FragmentTab1();
                return fragmenttab1;

            // Open FragmentTab2.java
            case 1:
                FragmentTab2 fragmenttab2 = new FragmentTab2();
                return fragmenttab2;

            // Open FragmentTab3.java
//            case 2:
//                FragmentTab3 fragmenttab3 = new FragmentTab3();
//                return fragmenttab3;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}