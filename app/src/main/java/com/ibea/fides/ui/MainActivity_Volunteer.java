package com.ibea.fides.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;


import com.ibea.fides.BaseActivity;
import com.astuetz.PagerSlidingTabStrip;

import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.UniversalPagerAdapter;

import java.util.ArrayList;

// Main organization page, nested with profile and shift tab.

public class MainActivity_Volunteer extends BaseActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("MV", "Created");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //TODO: Eventually will have user objects pacakaged in when navigating to this activity from a searched user list, typically indicating that the viewing user is not the same.

        Intent intent = getIntent();
        String name = intent.getStringExtra("username");
        Boolean isOrganization = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.KEY_ISORGANIZATION, false);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        //Profile only
        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        ArrayList<String> tabTitles = new ArrayList<String>();

        //TODO: This is going to need much more granular parsing

        if(isOrganization){
            tabTitles.add("Profile");
            fragmentList.add(new ProfileForVolunteerFragment());
            viewPager.setAdapter(new UniversalPagerAdapter(getSupportFragmentManager(), 1, tabTitles, fragmentList));
        }else {
            //User is volunteer, and this is their page

            tabTitles.add("Profile");
            tabTitles.add("My Shifts");
            tabTitles.add("Upcoming");
            tabTitles.add("History");
            fragmentList.add(new ProfileForVolunteerFragment());
            fragmentList.add(new ShiftSearchFragment());
            fragmentList.add(new ShiftsPendingForVolunteerFragment());
            fragmentList.add(new ShiftsCompletedForVolunteerFragment());
            viewPager.setAdapter(new UniversalPagerAdapter(getSupportFragmentManager(), 4, tabTitles, fragmentList));
        }


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }

}
