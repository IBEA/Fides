package com.ibea.fides.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.MainPagerAdapter_Organization;
import com.ibea.fides.adapters.MainPagerAdapter_User;

import java.util.ArrayList;

public class MainActivity_Organization extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_profile);

        Intent intent = getIntent();
        String name = intent.getStringExtra("username");
        Boolean isOrganization = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.KEY_ISORGANIZATION, false);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        //TODO: This is going to need much more granular parsing

        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        ArrayList<String> tabTitles = new ArrayList<String>();

        if(!isOrganization){
            //User is not an organization
            Log.v(TAG, "User is not an organization");

            tabTitles.add("Profile");
            fragmentList.add(new ProfileForOrganizationFragment());
            viewPager.setAdapter(new MainPagerAdapter_User(getSupportFragmentManager(), 1, tabTitles, fragmentList));
        }else {
            //User is organization, and this is their page
            Log.v(TAG, "User is an organization");

            tabTitles.add("Profile");
            tabTitles.add("Upcoming");
            tabTitles.add("History");
            fragmentList.add(new ProfileForOrganizationFragment());
            fragmentList.add(new ShiftsPendingForOrganizationFragment());
            fragmentList.add(new ShiftsCompletedForOrganizationFragment());
            viewPager.setAdapter(new MainPagerAdapter_User(getSupportFragmentManager(), 3, tabTitles, fragmentList));
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }
}
