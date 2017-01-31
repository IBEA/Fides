package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alaina Traxler on 1/31/2017.
 */

public class FirebaseCompletedShiftViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    View mView;
    Context mContext;
    Shift mShift;
    Button mVolunteerButton;
    Button mCompleteButton;
    Boolean isOrganization;

    public FirebaseCompletedShiftViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindShift(String shiftId, Boolean _isOrganization) {
        isOrganization = _isOrganization;

        Log.v(">>>>>", "In Completed bindShift");

        //!! Change volunteer button to cancel button if organization !!


//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_SHIFTS).child(shiftId);

//        ref.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Shift shift = dataSnapshot.getValue(Shift.class);
//                mShift = shift;
//                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                if(mShift != null){
//                    if(isOrganization && mShift.getOrganizationID().equals(userID)){
//                        mVolunteerButton.setText("Delete");
//                        mCompleteButton.setVisibility(View.VISIBLE);
//                    }else{
//                        if(shift.getCurrentVolunteers().indexOf(userID) != -1){
//                            mVolunteerButton.setText("Cancel");
//                        }else{
//                            mVolunteerButton.setText("Volunteer");
//                        }
//                    }
//                    //!! Set text !!
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public void onClick(View view) {
//        final ArrayList<Shift> shifts = new ArrayList<>();
//        String function = mVolunteerButton.getText().toString();

//        if(view == mVolunteerButton) {
//            if(function.equals("Volunteer")){
//                claimShift();
//            }else if(function.equals("Cancel")){
//                quitShift();
//            }else if(function.equals("Delete")){
//                deleteShift(true);
//            }else{
//                // Breadcrumb for front end. You should be able to parcel up mShift and then pass it as an intent to ShiftDetailsActivity.
//            }
//        }
//        if(view == mCompleteButton) {
//            completeShift();
//        }
    }


}

