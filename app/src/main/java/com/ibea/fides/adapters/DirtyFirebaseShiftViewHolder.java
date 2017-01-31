package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Shift;
import com.ibea.fides.ui.ShiftsTestingActivity;
import com.ibea.fides.utils.RecyclerItemListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alaina Traxler on 1/25/2017.
 */

public class DirtyFirebaseShiftViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    Context mContext;
    Shift mShift;
    Button mVolunteerButton;
    Button mCompleteButton;
    Boolean isOrganization;

    public DirtyFirebaseShiftViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindShift(String shiftId, Boolean _isOrganization) {
        isOrganization = _isOrganization;

        //!! Change volunteer button to cancel button if organization !!

        final TextView organizationTextView = (TextView) mView.findViewById(R.id.textView_Organization);
        final TextView shortDescriptionTextView = (TextView) mView.findViewById(R.id.textView_ShortDescription);
        final TextView zipCodeTextView = (TextView) mView.findViewById(R.id.textView_Zip);
        mVolunteerButton = (Button) mView.findViewById(R.id.button_Volunteer);
        mCompleteButton = (Button) mView.findViewById(R.id.button_Complete);
        mVolunteerButton.setOnClickListener(this);
        mCompleteButton.setOnClickListener(this);
        mCompleteButton.setVisibility(View.GONE);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_SHIFTS).child(shiftId);
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shift shift = dataSnapshot.getValue(Shift.class);
                mShift = shift;
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(mShift != null){
                    if(isOrganization && mShift.getOrganizationID().equals(userID)){
                        mVolunteerButton.setText("Delete");
                        mCompleteButton.setVisibility(View.VISIBLE);
                    }else{
                        if(shift.getCurrentVolunteers().indexOf(userID) != -1){
                            mVolunteerButton.setText("Cancel");
                        }else{
                            mVolunteerButton.setText("Volunteer");
                        }
                    }
                    organizationTextView.setText(shift.getOrganizationName());
                    shortDescriptionTextView.setText(shift.getShortDescription());
                    zipCodeTextView.setText(String.valueOf(shift.getZip()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        final ArrayList<Shift> shifts = new ArrayList<>();
        String function = mVolunteerButton.getText().toString();

        if(view == mVolunteerButton) {
            if(function.equals("Volunteer")){
                claimShift();
            }else if(function.equals("Cancel")){
                quitShift();
            }else if(function.equals("Delete")){
                deleteShift();
            }else{
                // Breadcrumb for front end. You should be able to parcel up mShift and then pass it as an intent to ShiftDetailsActivity.
            }
        }
        if(view == mCompleteButton) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_SHIFTS).child(shiftId);
            ref.addValueEventListener(new ValueEv
        }
    }

    public void deleteShift(){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String shiftId = mShift.getPushId();
        String organizationID = mShift.getOrganizationID();
        String zipcode = String.valueOf(mShift.getZip());

        List<String> userIds = mShift.getCurrentVolunteers();

        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child(zipcode).child(shiftId).removeValue();
        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_NODE_ORGANIZATIONS).child(organizationID).child(shiftId).removeValue();

        dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizationID).child(shiftId).removeValue();
        for(String user : userIds){
            dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(user).child(shiftId).removeValue();
        }

        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).removeValue();
    }

    public void quitShift(){
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String shiftId = mShift.getPushId();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shift shift = dataSnapshot.getValue(Shift.class);
                if(shift.getCurrentVolunteers().indexOf(userID) == -1){
                    Toast.makeText(mContext, "Not on shift", Toast.LENGTH_SHORT).show();
                }else{
                    mVolunteerButton.setText("Volunteer");

                    // Remove from shiftsPending for user
                    dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(userID).child(shiftId).removeValue();

                    //Remove user from list of volunteers and push to database
                    //!! Check to see what happens when sending an empty list !!
                    shift.removeVolunteer(userID);
                    dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("currentVolunteers").setValue(shift.getCurrentVolunteers());

                    //Check if shift was full. If so, repopulate to shiftsAvailable
                    //!! Currently untestable !!
                    if(shift.getMaxVolunteers() - shift.getCurrentVolunteers().size() == 1){
                        String zip = String.valueOf(shift.getZip());
                        String organizationID = shift.getOrganizationID();

                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child(zip).child(shiftId).setValue(shiftId);
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizationID).child(shiftId).setValue(shiftId);
                    }

                    Toast.makeText(mContext, "Removed from shift", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mContext, "Shift full", Toast.LENGTH_SHORT).show();
                }else{
                    mVolunteerButton.setText("Cancel");

                    // Assign to shiftsPending for user
                    dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(userID).child(shiftId).setValue(shiftId);

                    //Add user to list of volunteers and push to database
                    shift.addVolunteer(userID);
                    dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("currentVolunteers").setValue(shift.getCurrentVolunteers());


                    //check if shift has slots left. If not, remove from shiftsAvailable
                    String organizationID = shift.getOrganizationID();
                    String zip = String.valueOf(shift.getZip());

                    if(shift.getMaxVolunteers() - shift.getCurrentVolunteers().size() == 0){
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizationID).child(shiftId).removeValue();
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child(zip).child(shiftId).removeValue();
                    }

                    Toast.makeText(mContext, "Shift claimed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //!! Put protections in for shifts that have been claimed before the interface updates !!
    }
}