package com.eber.fides.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.eber.fides.adapters.OrganizationTabsPagerAdapter;
import com.ibea.fides.R;

public class OrganizationActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager mViewPager;
    private OrganizationTabsPagerAdapter mAdapter;
    private ActionBar mActionBar;
    //Tab tiles
    private String[] tabs = { "About", "Events", "Calendar"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);

        //Initilization
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mActionBar = getActionBar();
        mAdapter = new OrganizationTabsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mAdapter);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            mActionBar.addTab(mActionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
