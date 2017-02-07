package com.ibea.fides.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.view.menu.ListMenuItemView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ibea.fides.R.id.endTimeButton;

public class ShiftsCreateActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.textView_endTimeText) TextView mTextView_Until;
    @Bind(R.id.textView_startTimetext) TextView mTextView_Start;
    @Bind(R.id.editText_MaxVolunteers) EditText mEditText_MaxVolunteers;
    @Bind(R.id.button_LetsGo) Button mButton_LetsGo;
    @Bind(R.id.editText_Description) EditText mEditText_Descritpion;
    @Bind(R.id.editText_ShortDescription) EditText mEditText_ShortDescritpion;
    @Bind(R.id.editText_Address) EditText mEditText_Address;
    @Bind(R.id.editText_City) EditText mEditText_City;
    @Bind(R.id.editText_State) EditText mEditText_State;
    @Bind(R.id.editText_Zip) EditText mEditText_Zip;
    @Bind(R.id.startTimeButton) Button startTimeButton;
    @Bind(R.id.textView_dateTextView) TextView mTextView_Date;
    @Bind(R.id.endTimeButton) Button endTimeButton;
    @Bind(R.id.calendarButton) Button calendarButton;

    private int mYear, mMonth, mDay, mHour, mMinute;

    Organization thisOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts_create);
        ButterKnife.bind(this);

        autoFill();

        mButton_LetsGo.setOnClickListener(this);
        startTimeButton.setOnClickListener(this);
        endTimeButton.setOnClickListener(this);
        calendarButton.setOnClickListener(this);
    }

    // !! Checks to make sure all fields are filled out correctly !!
    public boolean validateFields(){
        return true;
    }

    //Converts to military time
    public String convertTime(String _time, boolean _isChecked){
        int marker = _time.indexOf(":");
        int hour = Integer.parseInt(_time.substring(0, marker));
        String minutes = _time.substring(marker, _time.length());

        if(_isChecked && hour != 12) {
            hour = hour + 12;
        }else if (!_isChecked && hour == 12) {
            hour = 0;
        }

        _time = hour + minutes;
//        Toast.makeText(mContext, _time, Toast.LENGTH_SHORT).show();
        return _time;
    }

    // Reads all fields and returns constructed shift
    public Shift createShift(String _organizationName, String _pushId){
        String until = (mTextView_Start.getText().toString());
        String from = (mTextView_Until.getText().toString());
        int maxVolunteers = Integer.parseInt(mEditText_MaxVolunteers.getText().toString());
        String date = mTextView_Date.getText().toString();
        String description = mEditText_Descritpion.getText().toString();
        String shortDescription = mEditText_ShortDescritpion.getText().toString();
        String address = mEditText_Address.getText().toString();
        String city = mEditText_City.getText().toString();
        String state = mEditText_State.getText().toString();
        String zip = mEditText_Zip.getText().toString();

        return new Shift(from, until, date, description, shortDescription, maxVolunteers, _pushId, address, city, state, zip, _organizationName);
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
        dbShiftsAvailable.child(Constants.DB_SUBNODE_STATECITY).child(String.valueOf(_shift.getState())).child(String.valueOf(_shift.getCity())).child(shiftId).setValue(shiftId);
        Toast.makeText(mContext, "Shift created", Toast.LENGTH_SHORT).show();

        // Add shift to shiftsPending for organization
        dbShiftsPending.child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizagtionID).child(shiftId).setValue(shiftId);

        //clear fields
        mEditText_MaxVolunteers.getText().clear();
        mTextView_Until.setText("0:00");
        mTextView_Start.setText("0:00");
        mTextView_Date.setText("0-0-0000");
        mEditText_Address.getText().clear();
        mEditText_Descritpion.getText().clear();
        mEditText_ShortDescritpion.getText().clear();
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
                            mEditText_Address.setText(thisOrg.getStreetAddress());
                            mEditText_City.setText(thisOrg.getCityAddress());
                            mEditText_State.setText(thisOrg.getStateAddress());
                            mEditText_Zip.setText(thisOrg.getZipcode());
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
    @Override
    public void onClick(View v){

        if(v == startTimeButton){
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            mTextView_Start.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if(v == endTimeButton){
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            mTextView_Until.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if (v == calendarButton) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            mTextView_Date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }



        if(v == mButton_LetsGo){
            dbCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if((Boolean) dataSnapshot.child("isOrganization").getValue()){
                        String organizationName = dataSnapshot.child("name").getValue().toString();
                        Log.v("Here:", organizationName);

                        String pushId = dataSnapshot.child("pushId").getValue().toString();

                        Log.v("There:", pushId);

                        if(validateFields()){
                            //Harvest data
                            Shift shift = createShift(organizationName, pushId);

                            //Push data
                            pushData(shift);
                        }
                    }else {
                        Toast.makeText(mContext, "Only organizations can create shifts", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

}
