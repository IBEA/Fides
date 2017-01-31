package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ibea.fides.R;
import com.ibea.fides.adapters.OrganizationProfilePageAdapter;

public class OrganizationProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_profile);

        //        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.organizationProfilePager);
        // Set the ViewPagerAdapter into ViewPager
        viewPager.setAdapter(new OrganizationProfilePageAdapter(getSupportFragmentManager()));
    }
}
