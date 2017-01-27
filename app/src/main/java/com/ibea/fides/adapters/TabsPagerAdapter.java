package com.ibea.fides.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ibea.fides.R;
import com.ibea.fides.ui.SampleSlide;

/**
 * Created by N8Home on 1/26/17.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter (FragmentManager fm){
        super (fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                // Organization about fragment activity
                return SampleSlide.newInstance(R.layout.slide_1);
            case 1:
                // Organization event fragment activity
                return SampleSlide.newInstance(R.layout.slide_2);
            case 2:
                // Organization calendar fragment activity
              return SampleSlide.newInstance(R.layout.slide_3);

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
