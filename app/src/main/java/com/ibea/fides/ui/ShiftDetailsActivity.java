package com.ibea.fides.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.FirebaseVolunteerViewHolder;
import com.ibea.fides.adapters.NewShiftSearchAdapter;
import com.ibea.fides.adapters.OrganizationListAdapter;
import com.ibea.fides.adapters.VolunteerListAdapter;
import com.ibea.fides.models.Shift;
import com.ibea.fides.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftDetailsActivity extends BaseActivity implements View.OnClickListener{
    private Shift mShift;
    private ArrayList<User> mVolunteers = new ArrayList<>();
    private ArrayList<String> mVolunteerIds = new ArrayList<>();
    private RecyclerView.Adapter mRecyclerAdapter;

    private int mYear, mMonth, mDay, mHour, mMinute;

    int rank;
    @Bind(R.id.textView_OrgName) TextView mOrgName;
    @Bind(R.id.editButton) TextView mEditButton;
    @Bind(R.id.finishEditButton) TextView mFinishEditButton;
    @Bind(R.id.textView_Date) TextView mDate;
    @Bind(R.id.textView_StartTime) TextView mTimeStart;
    @Bind(R.id.textView_EndTime) TextView mTimeEnd;
    @Bind(R.id.textView_Description) TextView mDescription;
    @Bind(R.id.editText_Description) EditText mDescriptionInput;
    @Bind(R.id.textView_Address) TextView mAddress;
    @Bind(R.id.editText_Address) EditText mAddressInput;
    @Bind(R.id.editText_City) EditText mCityInput;
    @Bind(R.id.editText_Zipcode) EditText mZipcodeInput;
    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.textView_Header) TextView mHeaderOne;
    @Bind(R.id.textView_Instructions) TextView mInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_details);
        ButterKnife.bind(this);

        mFinishEditButton.setOnClickListener(this);
        mDescriptionInput.setVisibility(View.GONE);
        mAddressInput.setVisibility(View.GONE);
        mCityInput.setVisibility(View.GONE);
        mZipcodeInput.setVisibility(View.GONE);
        mFinishEditButton.setVisibility(View.GONE);


        mShift = Parcels.unwrap(getIntent().getParcelableExtra("shift"));

        mOrgName.setText(mShift.getOrganizationName());

        mEditButton.setOnClickListener(this);

        if(mShift.getStartDate().equals(mShift.getEndDate())){
            mDate.setText(mShift.getStartDate());
        } else{
            mDate.setText(mShift.getStartDate() + " to " + mShift.getEndDate());
        }

        mTimeStart.setText(mShift.getStartTime() + " to ");
        mTimeEnd.setText(mShift.getEndTime());
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

    @Override
    public void onClick(View view) {
        if(view == mEditButton) {
            mDescriptionInput.setVisibility(View.VISIBLE);
            mDescriptionInput.setText(mDescription.getText());
            mDescription.setVisibility(View.GONE);
            mAddressInput.setVisibility(View.VISIBLE);
            mAddressInput.setText(mAddress.getText());
            mAddress.setVisibility(View.GONE);
            mCityInput.setVisibility(View.VISIBLE);
            mCityInput.setText(mShift.getCity());
            mZipcodeInput.setVisibility(View.VISIBLE);
            mZipcodeInput.setText(mShift.getZip());
            mEditButton.setVisibility(View.GONE);
            mFinishEditButton.setVisibility(View.VISIBLE);
            mTimeStart.setOnClickListener(this);
            mTimeEnd.setOnClickListener(this);
        }
        if(view == mFinishEditButton) {
            mTimeStart.setOnClickListener(null);
            mTimeEnd.setOnClickListener(null);

            mEditButton.setVisibility(View.VISIBLE);
            mFinishEditButton.setVisibility(View.GONE);

            DatabaseReference dbShiftsAvailable = db.child(Constants.DB_NODE_SHIFTSAVAILABLE).child("stateCity");

            dbShiftsAvailable.child(mShift.getState()).child(mShift.getCity()).child(mShift.getPushId()).removeValue();

            mShift.setDescription(mDescriptionInput.getText().toString());
            mShift.setStreetAddress(mAddressInput.getText().toString());
            mShift.setCity(mCityInput.getText().toString());
            mShift.setZip(mZipcodeInput.getText().toString());
            mShift.setStartTime(mTimeStart.getText().toString());
            mShift.setEndTime(mTimeEnd.getText().toString());

            mDescriptionInput.setVisibility(View.GONE);
            mDescription.setText(mShift.getDescription());
            mDescription.setVisibility(View.VISIBLE);

            mAddressInput.setVisibility(View.GONE);
            mAddress.setText(mShift.getStreetAddress());
            mAddress.setVisibility(View.VISIBLE);

            mCityInput.setVisibility(View.GONE);
            mZipcodeInput.setVisibility(View.GONE);

            mEditButton.setVisibility(View.VISIBLE);
            mFinishEditButton.setVisibility(View.GONE);

            dbShifts.child(mShift.getPushId()).setValue(mShift);
            String searchKey = mShift.getStartDate() + "|" + mShift.getStartTime() + "|" + mShift.getOrganizationName().toLowerCase() + "|" + mShift.getZip() + "|";

            dbShiftsAvailable.child(mShift.getState()).child(mShift.getCity()).child(mShift.getPushId()).setValue(searchKey);

            searchKey = mShift.getOrganizationName() + "|" + mShift.getZip() + "|" + mShift.getCity() + "|" + mShift.getState();
            DatabaseReference dbSearch = db.child(Constants.DB_NODE_SEARCH);
            dbSearch.child("organizations").child(mShift.getOrganizationID()).setValue(searchKey);


        }
        if(view == mTimeStart) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.TimePicker, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mTimeStart.setText(convertTime( hourOfDay + ":" + minute));
                }
            }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        if(view == mTimeEnd) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.TimePicker,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            mTimeEnd.setText( convertTime( hourOfDay + ":" + minute) );
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }


    public String convertTime(String _time) {
        int marker = _time.indexOf(":");
        int hour = Integer.parseInt(_time.substring(0, marker));
        String minutes = _time.substring(marker, _time.length());

        Log.d("Let's Rip it!", "Test This" + minutes.substring(1));
        if(Integer.parseInt(minutes.substring(1)) < 10){
            minutes = ":0" + minutes.substring(1);
        }

        _time = hour + minutes;

        return _time;

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