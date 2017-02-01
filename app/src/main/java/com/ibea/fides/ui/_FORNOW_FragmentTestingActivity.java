package com.ibea.fides.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;

public class _FORNOW_FragmentTestingActivity extends BaseActivity {
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_testing);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(0);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        private String tabtitles[] = new String[] { "Volunteer Opportunites", "Upcoming Shifts (Volunteers)", "Upcoming Shifts (Organizations)", "Completed Shifts (Volunteer)", "Completed Shifts (Organization)" };

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return ShiftSearchFragment.newInstance(0, "Volunteer Opportunities");
                case 1:
                    return ShiftsPendingForVolunteerFragment.newInstance(0, "Pending Shifts");
                case 2:
                    return ShiftsPendingForOrganizationFragment.newInstance(0, "Pending Shifts for Orgs");
                case 3:
                    return ShiftsCompletedForVolunteerFragment.newInstance(0, "Completed Shifts for Volunteers");
                case 4:
                    return ShiftsCompletedForOrganizationFragment.newInstance(0, "Completed Shifts for Organizations");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return tabtitles[position];
        }

    }
}
