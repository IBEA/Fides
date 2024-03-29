package com.ibea.fides.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.Shift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;



public class ShiftsCreateActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    @Bind(R.id.startTimeInput) TextView mStartTimeInput;
    @Bind(R.id.endTimeInput) TextView mEndTimeInput;
    @Bind(R.id.startDateInput) TextView mStartDateInput;
    @Bind(R.id.endDateInput) TextView mEndDateInput;
    @Bind(R.id.volunteerSizeInput) EditText mVolunteerSizeInput;
    @Bind(R.id.shortDescriptionInput) EditText mShortDescriptionInput;
    @Bind(R.id.longDescriptionInput) EditText mLongDescriptionInput;
    @Bind(R.id.streetInput) EditText mStreetInput;
    @Bind(R.id.cityInput) EditText mCityInput;
    @Bind(R.id.trustInput) Spinner mTrustInput;
    @Bind(R.id.stateSpinner) Spinner mStateSpinner;
    @Bind(R.id.zipcodeInput) EditText mZipcodeInput;
    @Bind(R.id.button_CreateShift) FloatingActionButton mSubmitButton;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private String mStartTime, mEndTime, mStartDate, mEndDate, mShortDescription, mSTrust, mLongDescription, mStreet, mCity, mState, mZipcode, Volunteertest;
    private int mVolunteerSize, mMinTrust;

    Organization thisOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_create);
        ButterKnife.bind(this);

        autoFill();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.states_array,R.layout.custom_spinner_item_settings);
        adapter.setDropDownViewResource(R.layout.custom_spinner_list_settings);
        mStateSpinner.setAdapter(adapter);

        Integer[] items = new Integer[]{0,25,50,75};

        ArrayAdapter<Integer> trustAdapter = new ArrayAdapter<Integer>(this,R.layout.custom_spinner_list_settings, items);
        mTrustInput.setAdapter(trustAdapter);


        mStartTimeInput.setOnClickListener(this);
        mEndTimeInput.setOnClickListener(this);
        mStartDateInput.setOnClickListener(this);
        mEndDateInput.setOnClickListener(this);
        mStateSpinner.setOnItemSelectedListener(this);
        mTrustInput.setOnItemSelectedListener(this);
        mSubmitButton.setOnClickListener(this);

        setTitle("Post Opportunity");
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

    public void autoFill() {
        dbOrganizations.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisOrg = dataSnapshot.getValue(Organization.class);
                mStreetInput.setText(thisOrg.getStreetAddress());
                mCityInput.setText(thisOrg.getCityAddress());
                mZipcodeInput.setText(thisOrg.getZipcode());

                String state = thisOrg.getStateAddress();
                Resources res = getResources();
                String[] states = res.getStringArray(R.array.states_array);
                int index = Arrays.asList(states).indexOf(state);
                mStateSpinner.setSelection(index);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(view == mStateSpinner) {
            mState = parent.getItemAtPosition(pos).toString();
        } else if(view == mTrustInput) {
            mMinTrust = Integer.parseInt(parent.getItemAtPosition(pos).toString());
        }

    }

    @Override
    public void onClick(View v) {
        if (v == mStartTimeInput) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);

            mMinute = c.get(Calendar.MINUTE);


            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.TimePicker, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mStartTime = convertTime(hourOfDay + ":" + minute);
                    mStartTimeInput.setText(mStartTime);
                }
            }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if (v == mEndTimeInput) {
            // Auto-populate time for picker
            mEndTime = mStartTime;
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.TimePicker,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            mEndTime = convertTime(hourOfDay + ":" + minute);
                            mEndTimeInput.setText(mEndTime);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if (v == mStartDateInput) {
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
                        mStartDate = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                        mStartDateInput.setText(mStartDate);

                        ///////////////////////////////////////
                        // Compare dates: if invalid, update end date
                        if(mEndDate != null) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                                Date date1 = sdf.parse(mStartDate);
                                Date date2 = sdf.parse(mEndDate);

                                if(date1.after(date2)) {
                                    mEndDate = mStartDate;
                                    mEndDateInput.setText(mEndDate);
                                }
                            } catch (ParseException ex){
                                ex.printStackTrace();
                                mEndDate = mStartDate;
                                mEndDateInput.setText(mEndDate);
                            }
                        }
                        else {
                            mEndDate = mStartDate;
                            mEndDateInput.setText(mEndDate);
                        }
                    }
                }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }


        if (v == mEndDateInput) {
            // Auto-populate date for picker
            mMonth = Integer.parseInt(mEndDate.substring(0, mEndDate.indexOf("-"))) - 1;
            mDay = Integer.parseInt((mEndDate.substring(mEndDate.indexOf("-") + 1, mEndDate.lastIndexOf("-"))));
            mYear = Integer.parseInt((mEndDate.substring(mEndDate.lastIndexOf("-") + 1)));

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.TimePicker,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mEndDate = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                            mEndDateInput.setText(mEndDate);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if (v == mSubmitButton) {
            if (validateFields()) {
                Shift shift = new Shift(mStartTime, mEndTime, mStartDate, mEndDate, mLongDescription, mShortDescription, mVolunteerSize, mMinTrust, uId, mStreet, mCity, mState, mZipcode, thisOrg.getName());

                //Push data
                pushData(shift);
            }
        }
    }

    public boolean validateFields(){

        mStartTime = mStartTimeInput.getText().toString();
        mEndTime = mEndTimeInput.getText().toString();
        mStartDate = mStartDateInput.getText().toString();
        mEndDate = mEndDateInput.getText().toString();
        Volunteertest = mVolunteerSizeInput.getText().toString();
        mShortDescription = mShortDescriptionInput.getText().toString();
        mLongDescription = mLongDescriptionInput.getText().toString();
        mSTrust = mTrustInput.getSelectedItem().toString();
        mStreet = mStreetInput.getText().toString();
        mCity = mCityInput.getText().toString();
        mState = mStateSpinner.getSelectedItem().toString(); // Safety against creating org without ever selecting spinner, resulting in NULL state
        mZipcode = mZipcodeInput.getText().toString();


        mStartTimeInput.setError(null);
        mEndTimeInput.setError(null);
        mStartDateInput.setError(null);
        mEndDateInput.setError(null);


        if(Volunteertest.equals("")){
            mVolunteerSizeInput.setError("Please enter the #");
            return false;
        }
        else {
            mVolunteerSize = Integer.parseInt(Volunteertest);

            if(mVolunteerSize == 0) {
                Toast.makeText(mContext, "There must be at least one volunteer", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(mSTrust.equals("")) {
            return false;
        } else {
            mMinTrust = Integer.parseInt(mSTrust);
            if(mMinTrust > 100) {
                Toast.makeText(mContext, "Required Trust must be between 0 and 100", Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        if(mStartTime.equals("Time")) {
            Toast.makeText(mContext, "Make sure to enter a start time.", Toast.LENGTH_SHORT).show();
            mStartTimeInput.setError("Set Time please.");
            return false;
        }
        else if(mEndTime.equals("Time")){
            Toast.makeText(mContext, "Make sure to enter an end time.", Toast.LENGTH_SHORT).show();
            mEndTimeInput.setError("Set Time please.");
            return false;
        }
        else if(mStartDate.equals("Date")){
            Toast.makeText(mContext, "Make sure to enter a start date.", Toast.LENGTH_SHORT).show();
            mStartDateInput.setError("Set Date please.");
            return false;
        }
        else if(mEndDate.equals("Date")){
            mEndDateInput.setError("Set Date please.");
            return false;
        }
        else if(mShortDescription.equals("")){
            mShortDescriptionInput.setError("Please add a Short Description");
            return false;
        }
        else if(mLongDescription.equals("")){
            mLongDescriptionInput.setError("Please add a Long Description");
            return false;
        }
        else if( mStreet.equals("")){
            mStreetInput.setError("Please enter a Street");
            return false;
        }
        else if( mCity.equals("")){
            mCityInput.setError("Please enter a City");
            return false;
        }
        else if( mZipcode.equals("")){
            mZipcodeInput.setError("Please enter a Zipcode");
            return false;
        }

        return compareDate(mStartDate, mEndDate, mStartTime, mEndTime);

    }

    public void validateTime() {
        mStartTime = mStartTimeInput.getText().toString();
        mEndTime = mEndTimeInput.getText().toString();
        mStartDate = mStartDateInput.getText().toString();
        mEndDate = mEndDateInput.getText().toString();

        if(mStartTime.equals("") || mEndTime.equals("")) {

            mStartTimeInput.setError("Set Time please.");
            Toast.makeText(mContext, "Make sure to enter a time.", Toast.LENGTH_SHORT).show();
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

    public void pushData(Shift _shift){
        String organizagtionID = _shift.getOrganizationID();

        // Add shift to database
        DatabaseReference pushRef = dbShifts.push();
        String shiftId = pushRef.getKey();
        _shift.setPushId(shiftId);
        pushRef.setValue(_shift);

        //Build search key
        String extendedStartTime;

        if(_shift.getStartTime().length() == 4){
            extendedStartTime = "0" + _shift.getStartTime();
        }else{
            extendedStartTime = _shift.getStartTime();
        }

        String searchKey = _shift.getStartDate() + "|" + extendedStartTime + "|" + _shift.getOrganizationName().toLowerCase() + "|" + _shift.getZip() + "|";

        // Add shift to shiftsAvailable fields
//        dbShiftsAvailable.child(Constants.DB_SUBNODE_ZIPCODE).child(String.valueOf(_shift.getZip())).child(shiftId).setValue(searchKey);
        dbShiftsAvailable.child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizagtionID).child(shiftId).setValue(shiftId);
        dbShiftsAvailable.child(Constants.DB_SUBNODE_STATECITY).child(String.valueOf(_shift.getState())).child(_shift.getCity().toLowerCase()).child(shiftId).setValue(searchKey);

        // Create Toast, overriding background property of activity
        Toast.makeText(mContext, "Shift created", Toast.LENGTH_SHORT).show();

        // Add shift to shiftsPending for organization
        dbShiftsPending.child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizagtionID).child(shiftId).setValue(shiftId);

        mLongDescriptionInput.setText("");
        mShortDescriptionInput.setText("");
        mStartTimeInput.setText("Time");
        mEndTimeInput.setText("Time");
        mStartDateInput.setText("Date");
        mEndDateInput.setText("Date");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_organizationtutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

//          TODO: Set up an actual message
            builder.setMessage("This is the Shift Create Page. You can create multiple shifts in a row.");

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });


            AlertDialog dialog = builder.create();

            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

}
