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

/**
 * Created by Alaina Traxler on 1/25/2017.
 */

public class DirtyFirebaseShiftViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    Context mContext;
    Shift mShift;
    RecyclerItemListener transfer;
    Button mVolunteerButton;

    public DirtyFirebaseShiftViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindShift(String shiftID, RecyclerItemListener _transfer) {
        transfer = _transfer;

        //!! Change volunteer button to cancel button if organization !!

        final TextView organizationTextView = (TextView) mView.findViewById(R.id.textView_Organization);
        final TextView shortDescriptionTextView = (TextView) mView.findViewById(R.id.textView_ShortDescription);
        final TextView zipCodeTextView = (TextView) mView.findViewById(R.id.textView_Zip);
        mVolunteerButton = (Button) mView.findViewById(R.id.button_Volunteer);

        mVolunteerButton.setOnClickListener(this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_SHIFTS).child(shiftID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shift shift = dataSnapshot.getValue(Shift.class);
                mShift = shift;
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(shift.getCurrentVolunteers().indexOf(userID) != -1){
                    mVolunteerButton.setText("Cancel");
                }

                organizationTextView.setText(shift.getOrganizationName());
                shortDescriptionTextView.setText(shift.getShortDescription());
                zipCodeTextView.setText(String.valueOf(shift.getZip()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        final ArrayList<Shift> shifts = new ArrayList<>();

        if(view == mVolunteerButton){
            claimShift(mShift);
        }else{
            transfer.userItemClick(mShift, "unspecified");
        }

        //This is going into the FULL, UNFILTERED shifts list. There should (will) ultimately be a way to point this toward the correct node of shiftsAvailable.
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_SHIFTS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    shifts.add(snapshot.getValue(Shift.class));
                }

                int itemPosition = getLayoutPosition();

                //Rough code for kicking into the pager adapter that it looks like you guys are using.
//                Intent intent = new Intent(mContext, ShiftDetailsActivity.class);
//                intent.putExtra("position", itemPosition + "");
//                intent.putExtra("shifts", Parcels.wrap(shifts));

//                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void claimShift(Shift shift){
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String shiftID = shift.getPushID();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shift shift = dataSnapshot.getValue(Shift.class);
                if(shift.getMaxVolunteers() - shift.getCurrentVolunteers().size() <= 0){
                    Toast.makeText(mContext, "Shift full", Toast.LENGTH_SHORT).show();
                }else{
                    // Assign to shiftsPending for user
                    dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(userID).child(shiftID).setValue(shiftID);

                    //Add user to list of volunteers and push to database
                    shift.addVolunteer(userID);
                    dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftID).child("currentVolunteers").setValue(shift.getCurrentVolunteers());


                    //check if shift has slots left. If not, remove from shiftsAvailable
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