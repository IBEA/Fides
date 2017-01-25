package com.ibea.fides.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShiftsCreateActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.editText_From) EditText mEditText_From;
    @Bind(R.id.editText_Until) EditText mEditText_Until;
    @Bind(R.id.editText_Date) EditText mEditText_Date;
    @Bind(R.id.editText_MaxVolunteers) EditText mEditText_MaxVolunteers;
    @Bind(R.id.button_LetsGo) Button mButton_LetsGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts_create);
        ButterKnife.bind(this);

        mButton_LetsGo.setOnClickListener(this);
    }

    // Checks to make sure all fields ar efilled out correctly
    public boolean validateFields(){

        return true;
    }

    @Override
    public void onClick(View v){
        if(v == mButton_LetsGo){
            if(validateFields()){
                //Harvest data
                String from = mEditText_From.getText().toString();
                String until = mEditText_Until.getText().toString();
                int maxVolunteers = Integer.parseInt(mEditText_MaxVolunteers.getText().toString());
                String date = mEditText_Date.getText().toString();

                //Push data
                DatabaseReference pushRef = dbShifts.push();
                pushRef.child("test").setValue("test");
                pushRef.child(Constants.DB_FIELD_UNTIL).setValue(until);
                pushRef.child(Constants.DB_FIELD_MAXVOLUNTEERS).setValue(maxVolunteers);
                pushRef.child(Constants.DB_FIELD_DATE).setValue(date);
                Toast.makeText(mContext, Constants.DB_NODE_SHIFTS, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
