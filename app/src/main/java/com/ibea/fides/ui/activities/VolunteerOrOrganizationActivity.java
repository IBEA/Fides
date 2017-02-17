package com.ibea.fides.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;

import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

//No need to extend BaseActivity
public class VolunteerOrOrganizationActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.button_Organization) Button mButton_Organization;
    @Bind(R.id.button_Volunteer) Button mButton_Volunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_or_organization);
        ButterKnife.bind(this);

        mButton_Organization.setOnClickListener(this);
        mButton_Volunteer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mButton_Volunteer){
            Intent intent = new Intent(this, CreateVolunteerActivity.class);
            startActivity(intent);
        }else if(view == mButton_Organization){
            Intent intent = new Intent(this, CreateOrganizationActivity.class);
            startActivity(intent);
        }
    }
}
