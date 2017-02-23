package com.ibea.fides;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.models.Shift;
import com.ibea.fides.ui.activities.LogInActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Garrett on 2/22/2017.
 */

public class MyAlarmBroadcastReceiver extends BroadcastReceiver {

    private Shift thisShift;
    DatabaseReference dbShifts;


    @Override
    public void onReceive(Context context, Intent intent ) {

        FirebaseAuth mAuth;
        FirebaseUser mCurrentUser;

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser != null) {

            DatabaseReference db;
            DatabaseReference dbShiftsPending;

            db = FirebaseDatabase.getInstance().getReference();
            dbShiftsPending = db.child(Constants.DB_NODE_SHIFTSPENDING);
            dbShifts = db.child(Constants.DB_NODE_SHIFTS);

            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();

            final String uId;
            uId = mAuth.getCurrentUser().getUid();

            final Calendar c = Calendar.getInstance();

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            //Notification Logic
            final NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_done_black_24dp)
                            .setContentTitle("Fides")
                            .setPriority(0)
                            .setSound(alarmSound);
            //.setContentText("Your shift is coming up in one hour! " + c.get(DATE));

            mBuilder.setAutoCancel(true);

            Intent resultIntent = new Intent(context, LogInActivity.class);

            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);

            final NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            Log.e("Garrett", "checking uId " + uId);

            dbShiftsPending.child("volunteers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(uId)) {
                        Log.d("Don't see me!", "You should not see this!");
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
                                    String currentDate = format1.format(c.getTime());

                                    if (Integer.toString(c.get(Calendar.HOUR_OF_DAY) + 1).equals(startTime) && currentDate.equals(thisShift.getStartDate())) {
                                        mBuilder.setContentText("Your shift at " + thisShift.getOrganizationName() + " is coming up!");
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
}
