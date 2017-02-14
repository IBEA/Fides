package com.ibea.fides.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.SwipelessViewPager;
import com.ibea.fides.adapters.UniversalPagerAdapter;
import com.ibea.fides.models.Organization;
import com.ibea.fides.ui.fragments.ProfileForOrganizationFragment;
import com.ibea.fides.ui.fragments.ShiftsAvailableByOrganizationFragment;
import com.ibea.fides.ui.fragments.ShiftsCompletedForOrganizationFragment;
import com.ibea.fides.ui.fragments.ShiftsPendingForOrganizationFragment;

import org.parceler.Parcels;

import java.util.ArrayList;

public class OrganizationProfileActivity extends BaseActivity {
    Organization mOrganization;
    Boolean isOrganization;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MO", "Created");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_home);

        Intent intent = getIntent();
        isOrganization = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.KEY_ISORGANIZATION, false);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(intent.getExtras() != null){
            Log.d(">>>>>", "Found extras");
            mOrganization = Parcels.unwrap(intent.getExtras().getParcelable("organization"));

            setTitle(mOrganization.getName());
            populateTabs();

        }else{
            Log.d(">>>>>", "Did not find extras");
            FirebaseDatabase.getInstance().getReference().child(Constants.DB_NODE_ORGANIZATIONS).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mOrganization = dataSnapshot.getValue(Organization.class);
                    Log.d(TAG, mOrganization.getName());
                    setTitle(mOrganization.getName());
                    populateTabs();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    public void populateTabs(){
        SwipelessViewPager viewPager = (SwipelessViewPager) findViewById(R.id.viewpager);

        //TODO: This is going to need much more granular parsing

        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        ArrayList<String> tabTitles = new ArrayList<String>();

        if(!isOrganization){
            //User is not an organization
            Log.v(TAG, "User is not an organization");

            tabTitles.add("Profile");
            tabTitles.add("Opportunities");
            //TODO: Investigate using constructor instead of instance
            fragmentList.add(new ProfileForOrganizationFragment().newInstance(mOrganization));
            fragmentList.add(new ShiftsAvailableByOrganizationFragment().newInstance(mOrganization));
            viewPager.setAdapter(new UniversalPagerAdapter(getSupportFragmentManager(), 2, tabTitles, fragmentList));
        }else {
            if(mOrganization.getPushId().equals(currentUserId)) {
                //User is organization, and this is their page
                Log.v(TAG, "User is an organization");

                tabTitles.add("Profile");
                tabTitles.add("Pending");
                tabTitles.add("History");
                //TODO: Investigate using constructor instead of instance
                fragmentList.add(new ProfileForOrganizationFragment().newInstance(mOrganization));
                fragmentList.add(new ShiftsPendingForOrganizationFragment());
                fragmentList.add(new ShiftsCompletedForOrganizationFragment());
                viewPager.setAdapter(new UniversalPagerAdapter(getSupportFragmentManager(), 3, tabTitles, fragmentList));
            }else{
                //Usr is organization, but it is not their page
            }

        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the toastView pager to the tab strip
        tabsStrip.setViewPager(viewPager);

    }


}