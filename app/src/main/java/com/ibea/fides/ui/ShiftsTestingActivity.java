package com.ibea.fides.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ibea.fides.R;

import butterknife.ButterKnife;

public class ShiftsTestingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts_testing);
        ButterKnife.bind(this);
    }
}
