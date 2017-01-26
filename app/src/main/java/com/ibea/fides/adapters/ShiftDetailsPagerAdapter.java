package com.ibea.fides.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ibea.fides.models.Shift;
import com.ibea.fides.ui.ShiftDetailsFragment;

import java.util.ArrayList;

public class ShiftDetailsPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Shift> mShifts;

    public ShiftDetailsPagerAdapter(FragmentManager fm, ArrayList<Shift> restaurants) {
        super(fm);
        mShifts = restaurants;
    }

    @Override
    public Fragment getItem(int position) {
        return ShiftDetailsFragment.newInstance(mShifts.get(position));
    }

    @Override
    public int getCount() {
        return mShifts.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        return Organization.FindByOID(mShifts.get(position).getOID()).getName();
        return "IBEA";
    }
}
