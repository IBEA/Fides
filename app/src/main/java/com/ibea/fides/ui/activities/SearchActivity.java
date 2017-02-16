package com.ibea.fides.ui.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.astuetz.PagerSlidingTabStrip;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.SwipelessViewPager;
import com.ibea.fides.adapters.UniversalPagerAdapter;
import com.ibea.fides.ui.fragments.OrganizationSearchFragment;
import com.ibea.fides.ui.fragments.ShiftSearchFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity {
    Boolean isOrganization = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        isOrganization = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.KEY_ISORGANIZATION, false);
        populateTabs();

        setTitle("Search");
    }

    public void populateTabs(){

        SwipelessViewPager viewPager = (SwipelessViewPager) findViewById(R.id.viewpager);

        //Profile only
        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        ArrayList<String> tabTitles = new ArrayList<String>();


        if(isOrganization){
            //User is organization
            tabTitles.add("Organizations");
            fragmentList.add(new OrganizationSearchFragment());
            viewPager.setAdapter(new UniversalPagerAdapter(getSupportFragmentManager(), 1, tabTitles, fragmentList));
        }else {
            //User is volunteer
            tabTitles.add("Opportunities");
            tabTitles.add("Organizations");
            fragmentList.add(new ShiftSearchFragment());
            fragmentList.add(new OrganizationSearchFragment());
            viewPager.setAdapter(new UniversalPagerAdapter(getSupportFragmentManager(), 2, tabTitles, fragmentList));
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the toastView pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }
}
