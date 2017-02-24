package com.ibea.fides.ui.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.MyAlarmBroadcastReceiver;
import com.ibea.fides.R;
import com.ibea.fides.adapters.SwipelessViewPager;
import com.ibea.fides.adapters.UniversalPagerAdapter;
import com.ibea.fides.models.Volunteer;
import com.ibea.fides.ui.fragments.ProfileForVolunteerFragment;
import com.ibea.fides.ui.fragments.ShiftsCompletedForVolunteerFragment;
import com.ibea.fides.ui.fragments.ShiftsPendingForVolunteerFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;

// Main organization page, nested with profile and shift tab.

public class VolunteerProfileActivity extends BaseActivity{
    Volunteer mVolunteer;
    Boolean isOrganization;
    String currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_home);

        Intent intent = getIntent();
        isOrganization = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.KEY_ISORGANIZATION, false);

        Log.d(TAG, "isOrg? " + isOrganization);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(intent.getExtras() != null){
            //Volunteer passed in, display profile for passed user
            Log.d(TAG, "Volunteer not passed in");
            mVolunteer = Parcels.unwrap(intent.getExtras().getParcelable("volunteer"));

            Log.d(">EXTRAS>", mVolunteer.getName());

            setTitle(mVolunteer.getName());
            populateTabs();

        }else{
            //Volunteer not passed in. Display profile for logged user

            FirebaseDatabase.getInstance().getReference().child(Constants.DB_NODE_VOLUNTEERS).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mVolunteer = dataSnapshot.getValue(Volunteer.class);

                    setTitle(mVolunteer.getName());
                    populateTabs();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        if((mCurrentUser != null) && (!mIsOrganization)) {
            Intent intent2 = new Intent(getApplicationContext(), MyAlarmBroadcastReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT );
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), 60000, sender);
        }

    }

    public void populateTabs(){

        SwipelessViewPager viewPager = (SwipelessViewPager) findViewById(R.id.viewpager);

        //Profile only
        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        ArrayList<String> tabTitles = new ArrayList<String>();

        if(isOrganization){
            tabTitles.add("Profile");
            fragmentList.add(new ProfileForVolunteerFragment());
            viewPager.setAdapter(new UniversalPagerAdapter(getSupportFragmentManager(), 1, tabTitles, fragmentList));
        }else {
            //Volunteer is volunteer, and this is their page
            tabTitles.add("Profile");
            tabTitles.add("Pending");
            tabTitles.add("History");
            fragmentList.add(new ProfileForVolunteerFragment().newInstance(mVolunteer));
            fragmentList.add(new ShiftsPendingForVolunteerFragment());
            fragmentList.add(new ShiftsCompletedForVolunteerFragment());
            viewPager.setAdapter(new UniversalPagerAdapter(getSupportFragmentManager(), 3, tabTitles, fragmentList));
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the toastView pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }


}