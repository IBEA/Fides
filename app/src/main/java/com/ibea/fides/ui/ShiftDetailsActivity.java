package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
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
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    private FirebaseRecyclerAdapter mFirebaseAdapter;

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

        setUpFirebaseAdapter();
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, DirtyFirebaseVolunteerViewHolder>
                (String.class, R.layout.volunteer_list_item, DirtyFirebaseVolunteerViewHolder.class, dbShifts.child(mShift.getPushId()).child("currentVolunteers")) {

            @Override
            protected void populateViewHolder(DirtyFirebaseVolunteerViewHolder viewHolder, String userId, int position) {
                viewHolder.bindUser(userId);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
