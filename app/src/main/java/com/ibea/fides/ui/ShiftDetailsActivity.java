package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.adapters.FirebaseVolunteerViewHolder;
import com.ibea.fides.models.Shift;
import org.parceler.Parcels;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftDetailsActivity extends BaseActivity {
    Shift mShift;

    int rank;
    @Bind(R.id.textView_OrgName) TextView mOrgName;
    @Bind(R.id.textView_Date) TextView mDate;
    @Bind(R.id.textView_Time) TextView mTime;
    @Bind(R.id.textView_Description) TextView mDescription;
    @Bind(R.id.textView_Address) TextView mAddress;
    @Bind(R.id.unratedRecyclerView) RecyclerView mUnratedRecyclerView;
    @Bind(R.id.textView_Header) TextView mHeaderOne;
    @Bind(R.id.textView_Instructions) TextView mInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_details);
        ButterKnife.bind(this);

        mShift = Parcels.unwrap(getIntent().getParcelableExtra("shift"));

        mOrgName.setText(mShift.getOrganizationName());

        if(mShift.getStartDate().equals(mShift.getEndDate())){
            mDate.setText(mShift.getStartDate());
        } else{
            mDate.setText(mShift.getStartDate() + " to " + mShift.getEndDate());
        }

        mTime.setText(mShift.getStartTime() + " to " + mShift.getEndTime());
        mDescription.setText(mShift.getDescription());
        mAddress.setText(mShift.getStreetAddress());

        if(mIsOrganization) {
            //TODO: Db call to the shift to get rated and unrated. Combine and feed to recyclerView but keep originals
            setUpFirebaseAdapter();
            if(mShift.getComplete()) {
                //TODO: Put the itemtouch setup in here
            }
        } else {
            mHeaderOne.setVisibility(View.GONE);
            mInstructions.setVisibility(View.GONE);
        }

    }

    private void setUpFirebaseAdapter() {
        FirebaseRecyclerAdapter mFirebaseAdapterUnrated = new FirebaseRecyclerAdapter<String, FirebaseVolunteerViewHolder>
                (String.class, R.layout.list_item_volunteer, FirebaseVolunteerViewHolder.class, dbShifts.child(mShift.getPushId()).child("currentVolunteers")) {

            @Override
            protected void populateViewHolder(FirebaseVolunteerViewHolder viewHolder, String userId, int position) {
                viewHolder.bindUser(userId, mShift.getPushId(), position, false);
            }
        };
        mUnratedRecyclerView.setHasFixedSize(false);
        mUnratedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUnratedRecyclerView.setAdapter(mFirebaseAdapterUnrated);
    }
}