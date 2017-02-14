package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Shift;
import com.ibea.fides.ui.activities.ShiftDetailsActivity;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alaina Traxler on 1/25/2017.
 */

public class FirebaseShiftViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @Bind(R.id.textView_OrganizationName) TextView mTextView_OrganizationName;
    @Bind(R.id.textView_StartTime) TextView mTextView_StartTime;
    @Bind(R.id.textView_EndTime) TextView mTextView_EndTime;
    @Bind(R.id.textView_StartDate) TextView mTextView_StartDate;
    @Bind(R.id.textView_ShortDesc) TextView mTextView_ShortDesc;

    private View mView;
    private Context mContext;
    private Shift mShift;
    private String mOrigin;
    private Boolean mIsComplete;

    public FirebaseShiftViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindShift(final Shift shift, String _origin) {
        mOrigin = _origin;

        Log.d(mOrigin, " in bindShift");

        mShift = shift;
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(mShift != null) {
            //Change button to delete if user is an organization

            mTextView_OrganizationName.setText(shift.getOrganizationName());
            mTextView_StartTime.setText(shift.getStartTime());
            mTextView_EndTime.setText(shift.getEndTime());
            mTextView_StartDate.setText(shift.getStartDate());
            mTextView_ShortDesc.setText(shift.getShortDescription());
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, ShiftDetailsActivity.class);
        intent.putExtra("shift", Parcels.wrap(mShift));
        mContext.startActivity(intent);

    }

    // Avoided duplicate functionality in completeShift by adding boolean
    public void deleteShift(boolean _total){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String shiftId = mShift.getPushId();
        String organizationID = mShift.getOrganizationID();
        String zipcode = String.valueOf(mShift.getZip());
        String state = mShift.getState();
        String city = mShift.getCity();

        List<String> userIds = mShift.getCurrentVolunteers();

        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child(zipcode).child(shiftId).removeValue();
        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_STATECITY).child(state).child(city).child(shiftId).removeValue();
        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_NODE_ORGANIZATIONS).child(organizationID).child(shiftId).removeValue();

        dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizationID).child(shiftId).removeValue();
        for(String user : userIds){
            dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(user).child(shiftId).removeValue();
        }

        if(_total) {
            dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).removeValue();
        }

    }

    public void completeShift() {
        String shiftId = mShift.getPushId();
        mShift.setComplete(true);
        deleteShift(false);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        List<String> userIds = mShift.getCurrentVolunteers();

        for(String user: userIds) {
            dbRef.child(Constants.DB_NODE_SHIFTSCOMPLETE).child(Constants.DB_SUBNODE_VOLUNTEERS).child(user).child(shiftId).setValue(shiftId);
        }
        dbRef.child(Constants.DB_NODE_SHIFTSCOMPLETE).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(mShift.getOrganizationID()).child(shiftId).setValue(shiftId);
        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("complete").setValue(true);
    }

    public void quitShift(){
        Log.d(">>>>>", "in quitShift");
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String shiftId = mShift.getPushId();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shift shift = dataSnapshot.getValue(Shift.class);
                if(shift.getCurrentVolunteers().indexOf(userID) == -1){
                    Toast toast = Toast.makeText(mContext, "Not on shift", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.argb(150,0,0,0));
                    view.setPadding(30,30,30,30);
                    toast.setView(view);
                    toast.show();
                }else{
                    // Remove from shiftsPending for user
                    dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(userID).child(shiftId).removeValue();

                    //Remove user from list of volunteers and push to database
                    //!! Check to see what happens when sending an empty list !!
                    shift.removeVolunteer(userID);
                    dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("currentVolunteers").setValue(shift.getCurrentVolunteers());

                    //Check if shift was full. If so, repopulate to shiftsAvailable
                    if(shift.getMaxVolunteers() - shift.getCurrentVolunteers().size() == 1){
                        String zip = String.valueOf(shift.getZip());
                        String organizationID = shift.getOrganizationID();
                        String state = shift.getState();
                        String city = shift.getCity();

                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child(zip).child(shiftId).setValue(shiftId);
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_STATECITY).child(state).child(city).child(shiftId).setValue(shiftId);
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizationID).child(shiftId).setValue(shiftId);
                    }

                    Toast toast = Toast.makeText(mContext, "Removed from shift", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.argb(150,0,0,0));
                    view.setPadding(30,30,30,30);
                    toast.setView(view);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void claimShift(){
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String shiftId = mShift.getPushId();
        Log.v("In claimShift:", shiftId);
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shift shift = dataSnapshot.getValue(Shift.class);
                if(shift.getMaxVolunteers() - shift.getCurrentVolunteers().size() <= 0){
                    Toast toast = Toast.makeText(mContext, "Shift full", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.argb(150,0,0,0));
                    view.setPadding(30,30,30,30);
                    toast.setView(view);
                    toast.show();
                }else{
//                    mVolunteerButton.setText("Cancel");

                    // Assign to shiftsPending for user
                    dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(userID).child(shiftId).setValue(shiftId);

                    //Add user to list of volunteers and push to database
                    shift.addVolunteer(userID);
                    dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("currentVolunteers").setValue(shift.getCurrentVolunteers());


                    //check if shift has slots left. If not, remove from shiftsAvailable
                    String organizationID = shift.getOrganizationID();
                    String zip = String.valueOf(shift.getZip());
                    String state = shift.getState();
                    String city = shift.getCity();


                    if(shift.getMaxVolunteers() - shift.getCurrentVolunteers().size() == 0){
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_STATECITY).child(state).child(city).child(shiftId).removeValue();
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizationID).child(shiftId).removeValue();
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child(zip).child(shiftId).removeValue();
                    }

                    Toast toast = Toast.makeText(mContext, "Shift claimed!", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.argb(150,0,0,0));
                    view.setPadding(30,30,30,30);
                    toast.setView(view);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}