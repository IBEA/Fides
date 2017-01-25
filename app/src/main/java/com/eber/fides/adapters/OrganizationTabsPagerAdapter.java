package com.eber.fides.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.eber.fides.ui.OrganizationAboutFragment;
import com.eber.fides.ui.OrganizationCalendarFragment;
import com.eber.fides.ui.OrganizationEventFragment;

/**
 * Created by N8Home on 1/25/17.
 */

public class OrganizationTabsPagerAdapter extends FragmentPagerAdapter {

    public OrganizationTabsPagerAdapter (FragmentManager fm){
        super(fm);

    }
    @Override
    public Fragment getItem(int index){
        switch (index) {
            case 0:
                // Organization about fragment activity
                return new OrganizationAboutFragment();
            case 1:
                // Organization event fragment activity
                return new OrganizationEventFragment();
            case 2:
                // Organization calendar fragment activity
                return new OrganizationCalendarFragment();
        }
        return null;
    }

    @Override
    public int getCount(){
        // get item count - equal to number of tabs
        return 3;
    }
}
