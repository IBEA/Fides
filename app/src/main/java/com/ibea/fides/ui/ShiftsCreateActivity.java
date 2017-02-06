package com.ibea.fides.ui;

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
import android.widget.EditText;
import android.widget.Switch;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShiftsCreateActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.editText_From) EditText mEditText_From;
    @Bind(R.id.editText_Until) EditText mEditText_Until;
    @Bind(R.id.editText_Date) EditText mEditText_Date;
    @Bind(R.id.editText_MaxVolunteers) EditText mEditText_MaxVolunteers;
    @Bind(R.id.button_LetsGo) Button mButton_LetsGo;
    @Bind((R.id.switch_From)) Switch mSwitch_From;
    @Bind(R.id.switch_To) Switch mSwitch_To;
    @Bind(R.id.editText_Description) EditText mEditText_Descritpion;
    @Bind(R.id.editText_ShortDescription) EditText mEditText_ShortDescritpion;
    @Bind(R.id.editText_Address) EditText mEditText_Address;
    @Bind(R.id.editText_City) EditText mEditText_City;
    @Bind(R.id.editText_State) EditText mEditText_State;
    @Bind(R.id.editText_Zip) EditText mEditText_Zip;

    Organization thisOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts_create);
        ButterKnife.bind(this);

        autoFill();

        mButton_LetsGo.setOnClickListener(this);
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
        String from = convertTime(mEditText_From.getText().toString(), mSwitch_From.isChecked());
        String until = convertTime(mEditText_Until.getText().toString(), mSwitch_To.isChecked());

        int maxVolunteers = Integer.parseInt(mEditText_MaxVolunteers.getText().toString());
        String date = mEditText_Date.getText().toString();
        String description = mEditText_Descritpion.getText().toString();
        String shortDescription = mEditText_ShortDescritpion.getText().toString();
        String address = mEditText_Address.getText().toString();
        String city = mEditText_City.getText().toString();
        String state = mEditText_State.getText().toString();
        int zip = Integer.parseInt(mEditText_Zip.getText().toString());

        Shift shift = new Shift(from, until, date, description, shortDescription, maxVolunteers, _pushId, address, city, state, zip, _organizationName);

        return shift;
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
        Toast.makeText(mContext, "Shift created", Toast.LENGTH_SHORT).show();

        // Add shift to shiftsPending for organization
        dbShiftsPending.child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizagtionID).child(shiftId).setValue(shiftId);

        //clear fields
        mEditText_MaxVolunteers.getText().clear();
        mEditText_From.getText().clear();
        mEditText_Until.getText().clear();
        mEditText_Date.getText().clear();
        mEditText_Descritpion.getText().clear();
        mEditText_ShortDescritpion.getText().clear();
        mEditText_Zip.getText().clear();
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
        if(v == mButton_LetsGo){
            dbCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if((Boolean) dataSnapshot.child("isOrganization").getValue() == true){
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
