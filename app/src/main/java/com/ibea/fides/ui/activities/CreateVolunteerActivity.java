package com.ibea.fides.ui.activities;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

//No need to extend BaseActivity
public class CreateVolunteerActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.editText_Name) EditText mEditText_Name;
    @Bind(R.id.editText_City) EditText mEditText_City;
    @Bind(R.id.editText_State) EditText mEditText_State;
    @Bind(R.id.editText_Zip) EditText mEditText_Zip;
    @Bind(R.id.button_Submit) FloatingActionButton mButton_Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_volunteer);
        ButterKnife.bind(this);

        mButton_Submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mButton_Submit){
            if(isValid(mEditText_Name) && isValid(mEditText_City) && validateZip(mEditText_Zip)){

            }
        }
    }

    //Create volunteer
    public void createVolunteer(){


    }

    // Validators for inputs
    private boolean isValid(EditText field) {
        String catcher = field.getText().toString().trim();

        if (catcher.equals("")) {
            field.setError("Invalid");
            return false;
        }
        return true;
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
}
