package com.ibea.fides.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Shift;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts_create);
        ButterKnife.bind(this);

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

    @Override
    public void onClick(View v){
        if(v == mButton_LetsGo){
            if(validateFields()){
                //Harvest data
                String from = convertTime(mEditText_From.getText().toString(), mSwitch_From.isChecked());

                String until = convertTime(mEditText_Until.getText().toString(), mSwitch_To.isChecked());

                int maxVolunteers = Integer.parseInt(mEditText_MaxVolunteers.getText().toString());
                String date = mEditText_Date.getText().toString();
                String description = mEditText_Descritpion.getText().toString();
                String shortDescription = mEditText_ShortDescritpion.getText().toString();

                Shift shift = new Shift(from, until, date, description, shortDescription, maxVolunteers, "temp");

                //Push data
                DatabaseReference pushRef = dbShifts.push();
                pushRef.setValue(shift);
                Toast.makeText(mContext, "Shift created", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shifts_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        else if (id == R.id.action_home){
            Intent intent = new Intent(ShiftsCreateActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}
