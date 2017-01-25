package com.ibea.fides.ui;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.ibea.fides.R;

public class HomeActivity extends AppCompatActivity implements ListView.OnItemSelectedListener{

    private String[] mPages = new String[] {"Logout", "Create Shifts" , "page3" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter sideBarAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mPages);

        spinner.setAdapter(sideBarAdapter);

        spinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // use parent.getItemAtPosition(pos) to get the selected value

        Log.d("Sidebar Val", "Val:" + parent.getItemAtPosition(pos));

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
