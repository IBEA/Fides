package com.ibea.fides.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.VolunteerListAdapter;
import com.ibea.fides.models.Shift;
import com.ibea.fides.models.User;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftDetailsActivity extends BaseActivity implements View.OnClickListener{
    private Shift mShift;
    private ArrayList<User> mVolunteers = new ArrayList<>();
    private ArrayList<String> mVolunteerIds = new ArrayList<>();
    private RecyclerView.Adapter mRecyclerAdapter;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String mStartTime, mEndTime, mStartD, mEndD, mVolunteerSize, mShortDesc, mLongDesc, mStreet, mCity, mZipcode;
    int rank;
    @Bind(R.id.textView_OrgName) TextView mOrgName;
    @Bind(R.id.editButton) TextView mEditButton;
    @Bind(R.id.finishEditButton) TextView mFinishEditButton;
    @Bind(R.id.textView_StartDate) TextView mStartDate;
    @Bind(R.id.textView_EndDate) TextView mEndDate;
    @Bind(R.id.textView_StartTime) TextView mTimeStart;
    @Bind(R.id.textView_to) TextView mDateFiller;
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
    @Bind(R.id.editText_ShortDescription) EditText mShortDescription;
    @Bind(R.id.editText_VolunteerMax) EditText mVolMaxInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_details);
        ButterKnife.bind(this);

        mDescriptionInput.setVisibility(View.GONE);
        mAddressInput.setVisibility(View.GONE);
        mCityInput.setVisibility(View.GONE);
        mZipcodeInput.setVisibility(View.GONE);
        mFinishEditButton.setVisibility(View.GONE);
        mShortDescription.setVisibility(View.GONE);
        mVolMaxInput.setVisibility(View.GONE);

        mShift = Parcels.unwrap(getIntent().getParcelableExtra("shift"));

        mOrgName.setText(mShift.getOrganizationName());


        mStartDate.setText(mShift.getStartDate());
        mEndDate.setText(mShift.getEndDate());


        mTimeStart.setText(mShift.getStartTime());
        mTimeEnd.setText(mShift.getEndTime());
        mDescription.setText(mShift.getDescription());
        mAddress.setText(mShift.getStreetAddress());

        if(mIsOrganization) {
            mVolunteers.clear();


            mEditButton.setOnClickListener(this);

            mFinishEditButton.setOnClickListener(this);
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
            mEditButton.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {
        if(view == mEditButton) {
            mDescriptionInput.setVisibility(View.VISIBLE);
            mDescriptionInput.setText(mDescription.getText());
            mShortDescription.setText(mShift.getShortDescription());
            mDescription.setVisibility(View.GONE);
            mAddressInput.setVisibility(View.VISIBLE);

            mVolMaxInput.setVisibility(View.VISIBLE);
            mAddressInput.setText(mAddress.getText());
            mAddress.setVisibility(View.GONE);
            mCityInput.setVisibility(View.VISIBLE);
            mCityInput.setText(mShift.getCity());
            mZipcodeInput.setVisibility(View.VISIBLE);
            mZipcodeInput.setText(mShift.getZip());
            mEditButton.setVisibility(View.GONE);
            mFinishEditButton.setVisibility(View.VISIBLE);
            mShortDescription.setVisibility(View.VISIBLE);
            mShortDescription.setText(mShift.getShortDescription());

            mVolMaxInput.setText(String.valueOf(mShift.getMaxVolunteers()).toString());
            mTimeStart.setOnClickListener(this);
            mTimeEnd.setOnClickListener(this);
            mStartDate.setOnClickListener(this);
            mEndDate.setOnClickListener(this);

        }
        if(view == mFinishEditButton) {
            mTimeStart.setOnClickListener(null);
            mTimeEnd.setOnClickListener(null);
            mStartDate.setOnClickListener(null);
            mEndDate.setOnClickListener(null);

            mEditButton.setVisibility(View.VISIBLE);
            mFinishEditButton.setVisibility(View.GONE);


            validateFields();

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
        if(view == mStartDate) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.TimePicker,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            mStartDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                            mEndDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if(view == mEndDate) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.TimePicker,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            mEndDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
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


    // Validators
    public void validateFields(){


        mStartTime = mTimeStart.getText().toString();
        mEndTime = mTimeEnd.getText().toString();
        mStartD = mStartDate.getText().toString();
        mEndD = mEndDate.getText().toString();
        mVolunteerSize = mVolMaxInput.getText().toString();
        mShortDesc = mShortDescription.getText().toString();
        mLongDesc = mDescriptionInput.getText().toString();
        mStreet = mAddressInput.getText().toString();
        mCity = mCityInput.getText().toString();
        mZipcode = mZipcodeInput.getText().toString();

        Log.d("Justin", "Comparing Date");

        if (compareDate(mStartD, mEndD, mStartTime, mEndTime)) {

            DatabaseReference dbShiftsAvailable = db.child(Constants.DB_NODE_SHIFTSAVAILABLE).child("stateCity");

            dbShiftsAvailable.child(mShift.getState()).child(mShift.getCity()).child(mShift.getPushId()).removeValue();

            mShift.setDescription(mDescriptionInput.getText().toString());
            mShift.setStreetAddress(mAddressInput.getText().toString());
            mShift.setCity(mCityInput.getText().toString());
            mShift.setZip(mZipcodeInput.getText().toString());
            mShift.setStartTime(mTimeStart.getText().toString());
            mShift.setEndTime(mTimeEnd.getText().toString());
            mShift.setStartDate(mStartDate.getText().toString());
            mShift.setEndDate(mEndDate.getText().toString());
            mShift.setShortDescription(mShortDescription.getText().toString());
            mShift.setMaxVolunteers(Integer.parseInt(mVolMaxInput.getText().toString()));


            mShortDescription.setVisibility(View.GONE);
            mDescriptionInput.setVisibility(View.GONE);
            mVolMaxInput.setVisibility(View.GONE);
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

        }
    }

    public boolean compareDate(String dateOne, String dateTwo, String timeOne, String timeTwo) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

            Date date1 = sdf.parse(dateOne);
            Date date2 = sdf.parse(dateTwo);

            SimpleDateFormat sdfTime = new SimpleDateFormat("kk:mm");
            Date time1 = sdfTime.parse(timeOne);
            Date time2 = sdfTime.parse(timeTwo);

            if(date1.after(date2)) {
                Log.d("Justin", "Rejected at Date");
                Toast toast = Toast.makeText(mContext, "Make sure to enter an end date that is AFTER the start date.", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            } else if(date1.equals(date2)) {
                if(time1.after(time2) || time1.equals(time2)) {
                    Log.d("Justin", "Rejected at Time");
                    Toast toast = Toast.makeText(mContext, "Make sure to enter a start time that is AFTER the end time.", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.d("Justin Time One: ", time1 + "");
                    Log.d("Justin Time Two: ", time2 + "");
                    return false;
                }
                return true;
            }
            return true;
        } catch (ParseException ex){
            ex.printStackTrace();

        }
        return false;
    }
}