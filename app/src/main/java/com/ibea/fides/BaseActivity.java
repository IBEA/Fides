package com.ibea.fides;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.models.Shift;
import com.ibea.fides.ui.activities.AdminActivity;
import com.ibea.fides.ui.activities.FaqActivity;
import com.ibea.fides.ui.activities.IntroActivity;
import com.ibea.fides.ui.activities.IntroOrganizationActivity;
import com.ibea.fides.ui.activities.IntroVolunteerActivity;
import com.ibea.fides.ui.activities.LogInActivity;
import com.ibea.fides.ui.activities.OrganizationProfileActivity;
import com.ibea.fides.ui.activities.VolunteerProfileActivity;
import com.ibea.fides.ui.activities.OrganizationSettingsActivity;
import com.ibea.fides.ui.activities.SearchActivity;
import com.ibea.fides.ui.activities.VolunteerSettingsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity extends AppCompatActivity {

    // Database References
    public DatabaseReference db;
    public DatabaseReference dbShifts;
    public DatabaseReference dbUsers;
    public DatabaseReference dbOrganizations;
    public DatabaseReference dbPendingOrganizations;
    public DatabaseReference dbTags;
    public DatabaseReference dbShiftsAvailable;
    public DatabaseReference dbShiftsPending;
    public DatabaseReference dbVolunteers;

    // Auth references
    public FirebaseAuth mAuth;
    public FirebaseUser mCurrentUser;

    // Shared Preferences
    public SharedPreferences mSharedPreferences;
    public boolean mIsOrganization;

    // For Navigation
    public Context mContext;
    public String mCurrentLoc;

    public String TAG;
    public String uId;

    Shift thisShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Set Context and TAG for each Activity
        mContext = this;
        TAG = ">>>>>" + this.getClass().getSimpleName();

        // Set database references
        db = FirebaseDatabase.getInstance().getReference();
        dbShifts = db.child(Constants.DB_NODE_SHIFTS);
        dbUsers = db.child(Constants.DB_NODE_USERS);
        dbOrganizations = db.child(Constants.DB_NODE_ORGANIZATIONS);
        dbPendingOrganizations = db.child(Constants.DB_NODE_APPLICATIONS);
        dbTags = db.child(Constants.DB_NODE_TAGS);
        dbShiftsAvailable = db.child(Constants.DB_NODE_SHIFTSAVAILABLE);
        dbShiftsPending = db.child(Constants.DB_NODE_SHIFTSPENDING);
        dbVolunteers = db.child(Constants.DB_NODE_VOLUNTEERS);

        // Set auth references
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        if(mCurrentUser != null){
            uId = mAuth.getCurrentUser().getUid();
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            mIsOrganization = mSharedPreferences.getBoolean(Constants.KEY_ISORGANIZATION, false);
            Log.v(TAG, mAuth.getCurrentUser().getEmail());
        }else{
            this.getSharedPreferences("isOrganization", 0).edit().clear().apply();
            Log.v(TAG, "No user logged in");
        }

        if(mCurrentUser != null) {
            Timer timer = new Timer ();
            TimerTask hourlyTask = new TimerTask () {
                @Override
                public void run() {
                    sendNotification();
                }
            };

            // schedule the task to run starting now and then every hour...
            timer.schedule (hourlyTask, 0l, 1000*60*60);
        }


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mIsOrganization = mSharedPreferences.getBoolean(Constants.KEY_ISORGANIZATION, false);

    }

    // On Start Override
    @Override
    public void onStart() {
        super.onStart();
    }

    // On Stop Override
    @Override
    public void onStop() {
        super.onStop();
    }

    public void logout() {
        this.getSharedPreferences("isOrganization", 0).edit().clear().apply();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(BaseActivity.this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(mIsOrganization){
            inflater.inflate(R.menu.menu_organization, menu);
        } else{
            inflater.inflate(R.menu.menu_volunteer, menu);
        }

        final MenuItem admin = (MenuItem) menu.findItem(R.id.action_admin);
        if(mCurrentUser != null) {
            dbVolunteers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(uId)){
                        if(dataSnapshot.child(uId).child("isAdmin").getValue(Boolean.class) == false){
                            admin.setVisible(false);
                        }
                    }else{
                        admin.setVisible(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        else if (id == R.id.user_page) {
            if(mIsOrganization) {
                Log.d(">>>>>", "Moving to org");
                Intent intent = new Intent(mContext, OrganizationProfileActivity.class);
                startActivity(intent);
            } else {
                Log.d(">>>>>", "Moving to vol");
                Intent intent = new Intent(mContext, VolunteerProfileActivity.class);
                startActivity(intent);
            }
        }
        else if (id == R.id.action_admin) {
            Intent intent = new Intent(mContext, AdminActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.settings_page) {
            Intent intent = new Intent(mContext, VolunteerSettingsActivity.class);
            if(mIsOrganization) {
                intent = new Intent(mContext, OrganizationSettingsActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else if(id == R.id.search_page){
            Intent intent = new Intent(mContext, SearchActivity.class);
            startActivity(intent);
        }else if (id == R.id.action_volunteertutorial) {
//            Log.d("Justin", mContext + "");
//            Intent intent = new Intent(mContext, IntroActivity.class);
//            startActivity(intent);
        }else if (id == R.id.action_organizationtutorial) {
//            Intent intent = new Intent(mContext, IntroOrganizationActivity.class);
//            startActivity(intent);
        }else if (id == R.id.action_faq) {
            Intent intent = new Intent(mContext, FaqActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void sendNotification(){
        final Calendar c = Calendar.getInstance();

        //Notification Logic
        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_done_black_24dp)
                        .setContentTitle("Fides")
                        .setPriority(0);
        //.setContentText("Your shift is coming up in one hour! " + c.get(DATE));

        mBuilder.setAutoCancel(true);

        Intent resultIntent = new Intent(this, LogInActivity.class);

// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Log.e("Garrett" , "checking uId " + uId);

        dbShiftsPending.child("volunteers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.hasChild(uId)) {
                    Log.d("Don't see me!" , "You should not see this!");
                    for (DataSnapshot postSnapshot : snapshot.child(uId).getChildren()) {
                        Log.e("correct value", postSnapshot.getValue().toString());

                        dbShifts.child(postSnapshot.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.e("Test me now", "data" + dataSnapshot.getValue());
                                thisShift = dataSnapshot.getValue(Shift.class);

                                String startTime = thisShift.getStartTime();
                                startTime = startTime.substring(0, startTime.indexOf(":"));

                                SimpleDateFormat format1 = new SimpleDateFormat("M-dd-yyyy");
                                String currentDate = format1.format( c.getTime() );

                                if(Integer.toString(c.get(Calendar.HOUR_OF_DAY) + 1).equals(startTime) && currentDate.equals(thisShift.getStartDate()) )
                                {
                                    mBuilder.setContentText("Your shift at " + thisShift.getOrganizationName() + " is coming up in one hour! ");
                                    mNotifyMgr.notify(101, mBuilder.build());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    //notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


