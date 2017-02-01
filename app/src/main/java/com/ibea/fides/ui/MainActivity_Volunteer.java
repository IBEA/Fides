package com.ibea.fides.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;


import com.ibea.fides.BaseActivity;
import com.astuetz.PagerSlidingTabStrip;

import com.ibea.fides.R;
import com.ibea.fides.adapters.MainPagerAdapter_User;

// Main organization page, nested with profile and shift tab.

public class MainActivity_Volunteer extends BaseActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        String name = intent.getStringExtra("username");

        Log.d("name" , "Does this work?" + name);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainPagerAdapter_User(getSupportFragmentManager()));

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

    }

}
