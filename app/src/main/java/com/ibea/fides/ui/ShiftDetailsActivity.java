package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseVolunteerViewHolder;
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
    @Bind(R.id.textView_Zipcode) TextView mZip;
    @Bind(R.id.unratedRecyclerView) RecyclerView mUnratedRecyclerView;
    @Bind(R.id.ratedRecyclerView) RecyclerView mRatedRecyclerView;
    @Bind(R.id.textView2) TextView mHeaderOne;
    @Bind(R.id.textView8) TextView mHeaderTwo;
    private FirebaseRecyclerAdapter mFirebaseAdapterUnrated;
    private FirebaseRecyclerAdapter mFirebaseAdapterRated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_details);
        ButterKnife.bind(this);

        mShift = Parcels.unwrap(getIntent().getParcelableExtra("shift"));

        mOrgName.setText(mShift.getOrganizationName());
        mDate.setText(mShift.getDate());
        mTime.setText("From " + mShift.getFrom() + " to " + mShift.getUntil());
        mDescription.setText(mShift.getDescription());
        mZip.setText(Integer.toString(mShift.getZip()));

        if(mIsOrganization) {
            setUpFirebaseAdapterUnrated();
            setUpFirebaseAdapterRated();
        } else {
            mHeaderOne.setVisibility(View.GONE);
            mHeaderTwo.setVisibility(View.GONE);
        }

    }

    private void setUpFirebaseAdapterUnrated() {
        mFirebaseAdapterUnrated = new FirebaseRecyclerAdapter<String, DirtyFirebaseVolunteerViewHolder>
                (String.class, R.layout.volunteer_list_item, DirtyFirebaseVolunteerViewHolder.class, dbShifts.child(mShift.getPushId()).child("currentVolunteers")) {

            @Override
            protected void populateViewHolder(DirtyFirebaseVolunteerViewHolder viewHolder, String userId, int position) {
                viewHolder.bindUser(userId, mShift.getPushId(), position, false);
            }
        };
        mUnratedRecyclerView.setHasFixedSize(true);
        mUnratedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUnratedRecyclerView.setAdapter(mFirebaseAdapterUnrated);
    }

    private void setUpFirebaseAdapterRated() {
        mFirebaseAdapterRated = new FirebaseRecyclerAdapter<String, DirtyFirebaseVolunteerViewHolder>
                (String.class, R.layout.volunteer_list_item, DirtyFirebaseVolunteerViewHolder.class, dbShifts.child(mShift.getPushId()).child("ratedVolunteers")) {

            @Override
            protected void populateViewHolder(DirtyFirebaseVolunteerViewHolder viewHolder, String userId, int position) {
                viewHolder.bindUser(userId, mShift.getPushId(), position, true);
            }
        };
        mRatedRecyclerView.setHasFixedSize(true);
        mRatedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRatedRecyclerView.setAdapter(mFirebaseAdapterRated);
    }
}