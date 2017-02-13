package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.adapters.FirebaseVolunteerViewHolder;
import com.ibea.fides.adapters.NewShiftSearchAdapter;
import com.ibea.fides.adapters.OrganizationListAdapter;
import com.ibea.fides.adapters.VolunteerListAdapter;
import com.ibea.fides.models.Shift;
import com.ibea.fides.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftDetailsActivity extends BaseActivity {
    private Shift mShift;
    private ArrayList<User> mVolunteers = new ArrayList<>();
    private ArrayList<String> mVolunteerIds = new ArrayList<>();
    private RecyclerView.Adapter mRecyclerAdapter;

    int rank;
    @Bind(R.id.textView_OrgName) TextView mOrgName;
    @Bind(R.id.textView_Date) TextView mDate;
    @Bind(R.id.textView_Time) TextView mTime;
    @Bind(R.id.textView_Description) TextView mDescription;
    @Bind(R.id.textView_Address) TextView mAddress;
    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;
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
            mVolunteers.clear();

            mRecyclerAdapter = new VolunteerListAdapter(mContext, mVolunteers, mShift.getCurrentVolunteers(), mShift);
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            mVolunteerIds.addAll(mShift.getCurrentVolunteers());
            mVolunteerIds.addAll(mShift.getRatedVolunteers());
            for(String volunteerId : mVolunteerIds){
                fetchVolunteer(volunteerId);
            }

            if(!mShift.getComplete()){
                mInstructions.setVisibility(View.GONE);
            }

        }else {
            mHeaderOne.setVisibility(View.GONE);
            mInstructions.setVisibility(View.GONE);
        }

    }

    public void fetchVolunteer(String _volunteerId){
        //db call
            //update within call
        dbUsers.child(_volunteerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mVolunteers.add(user);
                mRecyclerAdapter.notifyItemInserted(mVolunteers.indexOf(user));
                if(mShift.getComplete()){
                    setRecyclerViewItemTouchListener();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                VolunteerListAdapter.VolunteerViewHolder currentViewHolder = (VolunteerListAdapter.VolunteerViewHolder) viewHolder;

                if(swipeDir == 8){
                    currentViewHolder.popup(3);
                }else if(swipeDir == 4){
                    currentViewHolder.popup(0);
                }

                mRecyclerAdapter.notifyDataSetChanged();
                setRecyclerViewItemTouchListener();
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                VolunteerListAdapter.VolunteerViewHolder currentViewHolder = (VolunteerListAdapter.VolunteerViewHolder) viewHolder;
                String volunteerId = currentViewHolder.getVolunteerId();

                //Check to see if volunteer with current viewholder is already rated, disable swipes if so.
                if(currentViewHolder.isUnrated()){
//                    Log.d(mVolunteers.get(mVolunteerIds.indexOf(volunteerId)).getName(), "is unswipeable");
                    return super.getSwipeDirs(recyclerView, viewHolder);
                }else{
//                    Log.d(mVolunteers.get(mVolunteerIds.indexOf(volunteerId)).getName(), "is swipeable");
                }
                return 0;
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}