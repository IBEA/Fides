package com.ibea.fides.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.ibea.fides.models.Volunteer;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftDetailsActivity extends BaseActivity implements View.OnClickListener{
    private Shift mShift;
    private ArrayList<Volunteer> mVolunteers = new ArrayList<>();
    private ArrayList<String> mVolunteerIds = new ArrayList<>();
    private RecyclerView.Adapter mRecyclerAdapter;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String mStartTime, mEndTime, mStartD, mEndD, mVolunteerSize, mShortDesc, mLongDesc, mStreet, mCity, mState, mZipcode;
    int rank;
    boolean mInEditMode = false;
    @Bind(R.id.textView_OrgName) TextView mOrgName;
    @Bind(R.id.editText_ShortDescription) EditText mShortDescriptionInput;
    @Bind(R.id.textView_StartTime) TextView mTimeStart;
    @Bind(R.id.textView_EndTime) TextView mTimeEnd;
    @Bind(R.id.textView_StartDate) TextView mStartDate;
    @Bind(R.id.textView_DateFiller) TextView mDateFiller;
    @Bind(R.id.textView_EndDate) TextView mEndDate;
    @Bind(R.id.textView_StreetAddress) TextView mStreetAddressOutput;
    @Bind(R.id.editText_StreetAddress) EditText mStreetAddressInput;
    @Bind(R.id.textView_AddressLine2) TextView mAddressLine2Output;
    @Bind(R.id.linearLayout_AddressLine2Input) LinearLayout mAddressLine2Input;
    @Bind(R.id.editText_City) EditText mCityInput;
    @Bind(R.id.spinner_State) Spinner mStateInput;
    @Bind(R.id.editText_Zip) EditText mZipInput;
    @Bind(R.id.textView_Description) TextView mDescriptionOutput;
    @Bind(R.id.editText_Description) EditText mDescriptionInput;
    @Bind(R.id.view_VolSectionDivider) View mVolSectionDivider;
    @Bind(R.id.textView_VolunteerCurrentNumber) TextView mVolCurrentNumber;
    @Bind(R.id.textView_VolunteerMax) TextView mVolMaxOutput;
    @Bind(R.id.editText_VolunteerMax) EditText mVolMaxInput;
    @Bind(R.id.textView_VolunteerListHeader) TextView mVolunteersListHeader;
    @Bind(R.id.textView_VolunteerListInstructions) TextView mVolunteersListInstructions;
    @Bind(R.id.unratedRecyclerView) RecyclerView mVolunteersListRecyclerView;
    @Bind(R.id.floatingActionButton_EditOrComplete) FloatingActionButton mEditOrCompleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_details);
        ButterKnife.bind(this);
        mAddressLine2Output.setOnClickListener(this);
        mStreetAddressOutput.setOnClickListener(this);
        setTitle("Volunteer Opportunity");
        mShift = Parcels.unwrap(getIntent().getParcelableExtra("shift"));
        InitializeSpinner();
        SetShiftDetails();
        SetUserDeterminateDetails();
    }

    private void SetShiftDetails() {
        // Org Name
        mOrgName.setText(mShift.getOrganizationName());
        // Time
        mTimeStart.setText(mShift.getStartTime());
        mTimeEnd.setText(mShift.getEndTime());
        // Date
        mStartDate.setText(mShift.getStartDate());
        if(mShift.getEndDate().equals(mShift.getStartDate())) {
            mDateFiller.setVisibility(View.GONE);
            mEndDate.setVisibility(View.GONE);
        }
        else {
            mEndDate.setText(mShift.getEndDate());
        }
        // Address Line 1
        mStreetAddressOutput.setText(mShift.getStreetAddress());
        // Address Line 2
        mAddressLine2Output.setText(mShift.getCity() + ", " + mShift.getState() + ", " + mShift.getZip());
        // Full Description
        mDescriptionOutput.setText(mShift.getDescription());
    }

    private void SetUserDeterminateDetails() {
        if(uId.equals(mShift.getOrganizationID())) {
            mEditOrCompleteButton.setVisibility(View.VISIBLE);
            mEditOrCompleteButton.setOnClickListener(this);

            SetUpVolList();

            if(mShift.getCurrentVolunteers().isEmpty())
                mVolCurrentNumber.setText("0/");
            else
                mVolCurrentNumber.setText(String.valueOf(mShift.getCurrentVolunteers().size()) + "/");
            mVolMaxOutput.setText(String.valueOf(mShift.getMaxVolunteers()));

            if(mShift.getComplete()){
                mVolunteersListInstructions.setVisibility(View.VISIBLE);
            }
        }
        else if(!mIsOrganization){
            mVolCurrentNumber.setVisibility(View.GONE);
            mVolMaxOutput.setVisibility(View.GONE);
            if(mShift.getComplete()) {
                SetUpVolList();
            }
            else {
                mVolSectionDivider.setVisibility(View.GONE);
                mVolunteersListHeader.setVisibility(View.GONE);
            }
        }
    }

    private void SetUpVolList() {
        mVolunteers.clear();

        mRecyclerAdapter = new VolunteerListAdapter(mContext, mVolunteers, mShift.getCurrentVolunteers(), mShift);
        mVolunteersListRecyclerView.setHasFixedSize(false);
        mVolunteersListRecyclerView.setAdapter(mRecyclerAdapter);
        mVolunteersListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mVolunteerIds.addAll(mShift.getCurrentVolunteers());
        mVolunteerIds.addAll(mShift.getRatedVolunteers());
        for(String volunteerId : mVolunteerIds){
            fetchVolunteer(volunteerId);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == mEditOrCompleteButton) {
            if(!mInEditMode) {
                mEditOrCompleteButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_done_black_24dp));

                mShortDescriptionInput.setVisibility(View.VISIBLE);
                mShortDescriptionInput.setText(mShift.getShortDescription());

                mDateFiller.setVisibility(View.VISIBLE);
                mEndDate.setVisibility(View.VISIBLE);
                mEndDate.setText(mShift.getEndDate());
                mTimeStart.setOnClickListener(this);
                mTimeEnd.setOnClickListener(this);
                mStartDate.setOnClickListener(this);
                mEndDate.setOnClickListener(this);

                mStreetAddressInput.setVisibility(View.VISIBLE);
                mStreetAddressInput.setText(mShift.getStreetAddress());
                mStreetAddressOutput.setVisibility(View.GONE);

                mAddressLine2Input.setVisibility(View.VISIBLE);
                mCityInput.setText(mShift.getCity());
                String state = mShift.getState();
                Resources res = getResources();
                String[] states = res.getStringArray(R.array.states_array);
                int index = Arrays.asList(states).indexOf(state);
                mStateInput.setSelection(index);
                mZipInput.setText(mShift.getZip());
                mAddressLine2Output.setVisibility(View.GONE);

                mDescriptionInput.setVisibility(View.VISIBLE);
                mDescriptionInput.setText(mShift.getDescription());
                mDescriptionOutput.setVisibility(View.GONE);

                mVolMaxInput.setVisibility(View.VISIBLE);
                mVolMaxInput.setText(String.valueOf(mShift.getMaxVolunteers()));
                mVolMaxOutput.setVisibility(View.GONE);

                mInEditMode = true;
            }
            else {
                validateFields();
                mInEditMode = false;
            }
        }
        if(view == mTimeStart) {
            // Auto-populate time for picker
            mStartTime = mShift.getStartTime();
            mHour = Integer.parseInt(mStartTime.substring(0,mStartTime.indexOf(":")));
            mMinute = Integer.parseInt((mStartTime.substring(mStartTime.indexOf(":") + 1)));

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
            // Auto-populate time for picker
            mEndTime = mShift.getEndTime();
            mHour = Integer.parseInt(mEndTime.substring(0,mEndTime.indexOf(":")));
            mMinute = Integer.parseInt((mEndTime.substring(mEndTime.indexOf(":") + 1)));

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
            // Auto-populate date for picker
            mStartD = mShift.getStartDate();
            mMonth = Integer.parseInt(mStartD.substring(0, mStartD.indexOf("-"))) -1;
            mDay = Integer.parseInt((mStartD.substring(mStartD.indexOf("-") + 1, mStartD.lastIndexOf("-"))));
            mYear = Integer.parseInt((mStartD.substring(mStartD.lastIndexOf("-") + 1)));

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.TimePicker,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mStartD = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                            mStartDate.setText(mStartD);

                            ///////////////////////////////////////
                            // Compare dates: if invalid, update end date
                            if(mEndD != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                                    Date date1 = sdf.parse(mShift.getStartDate());
                                    Date date2 = sdf.parse(mShift.getEndDate());

                                    if(date1.after(date2)) {
                                        mEndD = mStartD;
                                        mEndDate.setText(mEndD);
                                    }
                                } catch (ParseException ex){
                                    ex.printStackTrace();
                                    mEndD = mStartD;
                                    mEndDate.setText(mEndD);
                                }
                            }
                            else {
                                mEndD = mStartD;
                                mEndDate.setText(mEndD);
                            }





                            mEndDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if(view == mEndDate) {
            // Auto-populate date for picker
            mEndD = mShift.getEndDate();
            mMonth = Integer.parseInt(mEndD.substring(0, mEndD.indexOf("-"))) - 1;
            mDay = Integer.parseInt((mEndD.substring(mEndD.indexOf("-") + 1, mEndD.lastIndexOf("-"))));
            mYear = Integer.parseInt((mEndD.substring(mEndD.lastIndexOf("-") + 1)));


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
        if(view == mStreetAddressOutput) {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=" + mShift.getStreetAddress() + ", " + mShift.getCity() + ", " + mShift.getState()));
            startActivity(mapIntent);
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
        dbVolunteers.child(_volunteerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Volunteer volunteer = dataSnapshot.getValue(Volunteer.class);
                mVolunteers.add(volunteer);
                mRecyclerAdapter.notifyItemInserted(mVolunteers.indexOf(volunteer));
                if(mShift.getComplete()){
                    setRecyclerViewItemTouchListener();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void InitializeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.states_array,R.layout.custom_spinner_item_settings);
        adapter.setDropDownViewResource(R.layout.custom_spinner_list_settings);
        mStateInput.setAdapter(adapter);
        mStateInput.setSelection(0);
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
        itemTouchHelper.attachToRecyclerView(mVolunteersListRecyclerView);
    }


    // Validators
    public void validateFields(){

        mShortDesc = mShortDescriptionInput.getText().toString();
        mStartTime = mTimeStart.getText().toString();
        mEndTime = mTimeEnd.getText().toString();
        mStartD = mStartDate.getText().toString();
        mEndD = mEndDate.getText().toString();
        mStreet = mStreetAddressInput.getText().toString();
        mState = mStateInput.getSelectedItem().toString();
        mCity = mCityInput.getText().toString();
        if(validateZip(mZipInput)) {
            mZipcode = mZipInput.getText().toString();
        } else {
            Toast.makeText(mContext, "Please enter a proper zipcode", Toast.LENGTH_SHORT).show();
            return;
        }
        mLongDesc = mDescriptionInput.getText().toString();
        mVolunteerSize = mVolMaxInput.getText().toString();

        if(mVolunteerSize.equals("0")) {
            Toast.makeText(mContext, "Max Volunteers must be at least 1", Toast.LENGTH_SHORT).show();
            return;
        } else if(Integer.parseInt(mVolunteerSize) < mShift.getCurrentVolunteers().size()) {
            Toast.makeText(mContext, "There are currently more volunteers signed up than the new max", Toast.LENGTH_SHORT).show();
            return;
        }


        if (compareDate(mStartD, mEndD, mStartTime, mEndTime)) {

            DatabaseReference dbShiftsAvailable = db.child(Constants.DB_NODE_SHIFTSAVAILABLE).child("stateCity");

            dbShiftsAvailable.child(mShift.getState()).child(mShift.getCity()).child(mShift.getPushId()).removeValue();

            mShift.setShortDescription(mShortDescriptionInput.getText().toString());
            mShift.setStartTime(mTimeStart.getText().toString());
            mShift.setEndTime(mTimeEnd.getText().toString());
            mShift.setStartDate(mStartDate.getText().toString());
            mShift.setEndDate(mEndDate.getText().toString());
            mShift.setStreetAddress(mStreetAddressInput.getText().toString());
            mShift.setCity(mCityInput.getText().toString());
            mShift.setState(mStateInput.getSelectedItem().toString());
            mShift.setZip(mZipInput.getText().toString());
            mShift.setDescription(mDescriptionInput.getText().toString());
            mShift.setMaxVolunteers(Integer.parseInt(mVolMaxInput.getText().toString()));

            mEditOrCompleteButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_mode_edit_black_24dp));

            mShortDescriptionInput.setVisibility(View.GONE);

            mTimeStart.setOnClickListener(null);
            mTimeEnd.setOnClickListener(null);
            mStartDate.setOnClickListener(null);
            mEndDate.setOnClickListener(null);

            if(mShift.getEndDate().equals(mShift.getStartDate())) {
                mDateFiller.setVisibility(View.GONE);
                mEndDate.setVisibility(View.GONE);
            }

            mStreetAddressOutput.setVisibility(View.VISIBLE);
            mStreetAddressOutput.setText(mShift.getStreetAddress());
            mStreetAddressInput.setVisibility(View.GONE);

            mAddressLine2Output.setVisibility(View.VISIBLE);
            mAddressLine2Output.setText(mShift.getCity() + ", " + mShift.getState() + ", " + mShift.getZip());
            mAddressLine2Input.setVisibility(View.GONE);

            mDescriptionOutput.setVisibility(View.VISIBLE);
            mDescriptionOutput.setText(mShift.getDescription());
            mDescriptionInput.setVisibility(View.GONE);

            mVolMaxOutput.setVisibility(View.VISIBLE);
            mVolMaxOutput.setText(String.valueOf(mShift.getMaxVolunteers()));
            mVolMaxInput.setVisibility(View.GONE);

            dbShifts.child(mShift.getPushId()).setValue(mShift);
            String searchKey = mShift.getStartDate() + "|" + mShift.getStartTime() + "|" + mShift.getOrganizationName().toLowerCase() + "|" + mShift.getZip() + "|";

            // Remove from Shifts Available if new max is equal to current volunteers
            int newMax = Integer.parseInt(mVolunteerSize);
            int currentVolunteerSize = mShift.getCurrentVolunteers().size();
            if(newMax == currentVolunteerSize) {
                dbShiftsAvailable.child(mShift.getState()).child(mShift.getCity().toLowerCase()).child(mShift.getPushId()).removeValue();
                dbShiftsAvailable.child("organizations").child(mShift.getOrganizationID()).child(mShift.getPushId()).removeValue();
            }
            else {
                dbShiftsAvailable.child(mShift.getState()).child(mShift.getCity().toLowerCase()).child(mShift.getPushId()).setValue(searchKey);
            }

        }
        else {
            Toast.makeText(mContext, "Invalid Input", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean validateZip(EditText field){
        String onlyNumbers = "[0-9]+";
        String catcher = field.getText().toString().trim();

        if(catcher.length() != 0){
            if(catcher.length() == 5 && catcher.matches(onlyNumbers)){
                return true;
            }else{
                field.setError("Invalid");
                return false;
            }
        }else return true;
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

                Toast.makeText(mContext, "Make sure to enter an end date that is AFTER the start date.", Toast.LENGTH_SHORT).show();
                return false;
            } else if(date1.equals(date2)) {
                if(time1.after(time2) || time1.equals(time2)) {

                    Toast.makeText(mContext, "Make sure to enter a start time that is AFTER the end time.", Toast.LENGTH_SHORT).show();


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_volunteertutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

//          TODO: Set up an actual message
            builder.setMessage("This is the Shift Details Page. Pertinent shift information is listed here");

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Log.d("Justin", "Dismiss");
                }
            });


            AlertDialog dialog = builder.create();

            dialog.show();

        }  else if (id == R.id.action_organizationtutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

//          TODO: Set up an actual message
            builder.setMessage("This is the Shifts Pending Page. Pertinent shift information is listed here. You can edit this information by clicking the pencil icon. If the shift is completed, then you can rate volunteers by swiping right on them or mark volunteers as absent by swiping left.");

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Log.d("Justin", "Dismiss");
                }
            });


            AlertDialog dialog = builder.create();

            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }
}