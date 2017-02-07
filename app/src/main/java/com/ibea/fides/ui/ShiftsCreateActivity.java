package com.ibea.fides.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.view.menu.ListMenuItemView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.Shift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;



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
    @Bind(R.id.stateSpinner) Spinner mStateSpinner;
    @Bind(R.id.zipcodeInput) EditText mZipcodeInput;
    @Bind(R.id.submitShiftButton) Button mSubmitButton;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private String mStartTime, mEndTime, mStartDate, mEndDate, mShortDescription, mLongDescription, mStreet, mCity, mState, mZipcode;
    private int mVolunteerSize;

    Organization thisOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts_create);
        ButterKnife.bind(this);

        autoFill();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStateSpinner.setAdapter(adapter);

        mStartTimeInput.setOnClickListener(this);
        mEndTimeInput.setOnClickListener(this);
        mStartDateInput.setOnClickListener(this);
        mEndDateInput.setOnClickListener(this);
        mStateSpinner.setOnItemSelectedListener(this);
        mSubmitButton.setOnClickListener(this);
    }

        public String convertTime(String _time) {
            int marker = _time.indexOf(":");
            int hour = Integer.parseInt(_time.substring(0, marker));
            String minutes = _time.substring(marker, _time.length());

            Log.d("Let's Rip it!", "Test This" + minutes.substring(1));
            if(Integer.parseInt(minutes.substring(1)) < 10){
                minutes = ":0" + minutes.substring(1);
            }

            if(hour > 12) {
                hour = hour - 12;
                return hour + minutes + " P.M.";
            }
            else if (hour == 0) {
                hour = 12;

                _time = hour + minutes;
//        Toast.makeText(mContext, _time, Toast.LENGTH_SHORT).show();
                return _time + " A.M.";
            }
            else{
                return hour + minutes + " A.M.";
            }
        }

    public void autoFill() {
        dbCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((Boolean) dataSnapshot.child("isOrganization").getValue()) {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mState = parent.getItemAtPosition(pos).toString();
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
                    mStartTimeInput.setText(hourOfDay + ":" + minute);
                }
            }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if (v == mEndTimeInput) {
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

                            mEndTimeInput.setText(hourOfDay + ":" + minute);
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

                            mStartDateInput.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                            mEndDateInput.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }


        if (v == mEndDateInput) {
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

                            mEndDateInput.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if (v == mSubmitButton) {
            dbCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if ((Boolean) dataSnapshot.child("isOrganization").getValue()) {
                        String organizationName = dataSnapshot.child("name").getValue().toString();
                        Log.v("Here:", organizationName);

                        String pushId = dataSnapshot.child("pushId").getValue().toString();

                        Log.v("There:", pushId);

                        if (validateFields()) {
                            Shift shift = createShift(organizationName, pushId);

                            //Push data
                            pushData(shift);
                        }
                    } else {
                        Toast.makeText(mContext, "Only organizations can create shifts", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public boolean validateFields(){
        mStartTime = mStartTimeInput.getText().toString();
        mEndTime = mEndTimeInput.getText().toString();
        mStartDate = mStartDateInput.getText().toString();
        mEndDate = mEndDateInput.getText().toString();
        mVolunteerSize = Integer.parseInt(mVolunteerSizeInput.getText().toString());
        mShortDescription = mShortDescriptionInput.getText().toString();
        mLongDescription = mLongDescriptionInput.getText().toString();
        mStreet = mStreetInput.getText().toString();
        mCity = mCityInput.getText().toString();
        mZipcode = mZipcodeInput.getText().toString();

        if(mStartTime.equals("") || mEndTime.equals("") || mStartDate.equals("") || !(mVolunteerSize > 0) || mShortDescription.equals("") || mLongDescription.equals("") || mStreet.equals("") || mCity.equals("") || mZipcode.equals("")) {
            return false;
        }

        return compareDate(mStartDate, mEndDate);

    }

    public boolean compareDate(String dateOne, String dateTwo) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date date1 = sdf.parse(dateOne);
            Date date2 = sdf.parse(dateTwo);

            if(!date1.after(date2)) {
                return true;
            }

        } catch (ParseException ex){
            ex.printStackTrace();

        }
        return false;
    }


    // Reads all fields and returns constructed shift
    public Shift createShift(String _organizationName, String _pushId){
        return new Shift(mStartTime, mEndTime, mStartDate, mEndDate, mLongDescription, mShortDescription, mVolunteerSize, _pushId, mStreet, mCity, mState, mZipcode, _organizationName);

    }

    public void pushData(Shift _shift){
        String organizagtionID = _shift.getOrganizationID();

        // Add shift to database
        DatabaseReference pushRef = dbShifts.push();
        String shiftId = pushRef.getKey();
        _shift.setPushId(shiftId);
        pushRef.setValue(_shift);

        // Add shift to shiftsAvailable fields
        dbShiftsAvailable.child(Constants.DB_SUBNODE_ZIPCODE).child(String.valueOf(_shift.getZip())).child(shiftId).setValue(shiftId);
        dbShiftsAvailable.child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizagtionID).child(shiftId).setValue(shiftId);
        dbShiftsAvailable.child(Constants.DB_SUBNODE_STATE).child(String.valueOf(_shift.getState())).child(String.valueOf(_shift.getCity())).child(shiftId).setValue(shiftId);
        Toast.makeText(mContext, "Shift created", Toast.LENGTH_SHORT).show();

        // Add shift to shiftsPending for organization
        dbShiftsPending.child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizagtionID).child(shiftId).setValue(shiftId);

    }




}


