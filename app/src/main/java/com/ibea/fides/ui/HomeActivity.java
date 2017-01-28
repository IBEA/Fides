package com.ibea.fides.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.button_Dirty) Button mButton_Dirty;
    @Bind(R.id.button_Dirty_Logout) Button mButton_Dirty_Logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mButton_Dirty.setOnClickListener(this);
        mButton_Dirty_Logout.setOnClickListener(this);

        final Intent emptyIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Keep this to test intro
        //Intent intent = new Intent(HomeActivity.this , IntroActivity.class);
        //startActivity(intent);

        //Use the following block of code for real app performance -- Will only run intro once
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                if (isFirstStart) {
                    Intent i = new Intent(HomeActivity.this, IntroActivity.class);
                    startActivity(i);

                    SharedPreferences.Editor e = getPrefs.edit();

                    e.putBoolean("firstStart", false);

                    e.apply();
                }
            }
        });

        t.start();

        //end of block
    }

    @Override
    public void onClick(View v){
        if(v == mButton_Dirty){
            Intent intent = new Intent(mContext, ShiftsTestingActivity.class);
            startActivity(intent);
        }
        if(v == mButton_Dirty_Logout) {
            mUniversal.logout(mContext);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        else if (id == R.id.action_shifts){
            Intent intent = new Intent(HomeActivity.this, ShiftsCreateActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_dirty_shift_search) {
            Intent intent = new Intent(mContext, ShiftSearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    //Use this as a function Notification System
    private void Notify() {
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(this);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_arrow_back_white)
                .setTicker("This is the ticker!")
                .setContentTitle("Fides Alert!")
                .setContentText("Your Volunteer shift is coming up in 1 hour!")
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info")
                .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }

}
