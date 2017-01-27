package com.ibea.fides.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseShiftViewHolder;
import com.ibea.fides.models.Shift;
import com.ibea.fides.utils.RecyclerItemListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftsTestingActivity extends BaseActivity implements RecyclerItemListener{
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerItemListener mTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts_testing);
        ButterKnife.bind(this);

        mTransfer = this;

        setUpFirebaseAdapter();
    }

    @Override
    public void userItemClick(Object data, String view){
        Shift shift = (Shift) data;
        if(view.equals("volunteerButton")){
            claimShift(shift);
        }else{
            //!! Redirect to shift details!!
        }
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, DirtyFirebaseShiftViewHolder>
                (String.class, R.layout.dirty_shift_list_item, DirtyFirebaseShiftViewHolder.class, dbShiftsAvailable.child(Constants.DB_SUBNODE_ZIPCODE).child("97201")) {

            @Override
            protected void populateViewHolder(DirtyFirebaseShiftViewHolder viewHolder, String shiftID, int position) {
                viewHolder.bindShift(shiftID, mTransfer);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    public void claimShift(Shift shift){
        final String userID = mCurrentUser.getUid();
        final String shiftID = shift.getPushID();

        dbShifts.child(shiftID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shift shift = dataSnapshot.getValue(Shift.class);
                if(shift.getMaxVolunteers() - shift.getCurrentVolunteers().size() <= 0){
                    Toast.makeText(ShiftsTestingActivity.this, "Shift full", Toast.LENGTH_SHORT).show();
                }else{
                    // Assign to shiftsPending for user
                    dbShiftsPending.child(Constants.DB_SUBNODE_VOLUNTEERS).child(userID).child(shiftID).setValue(shiftID);

                    //Add user to list of volunteers and push to database
                    shift.addVolunteer(userID);
                    dbShifts.child(shiftID).child("currentVolunteers").setValue(shift.getCurrentVolunteers());


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }
}
