package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibea.fides.R;

/**
 * Created by N8Home on 1/30/17.
 */

public class OrganizationProfileFragment extends Fragment {
//    @Bind(R.id.button_Volunteer) Button mVolunteerButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.organizationprofilefragment, container, false);


//        mVolunteerButton.setOnClickListener(this);

        return view;
    }
//    @Override
//    public void onClick(View v) {
//        if (v == mVolunteerButton){
//            Toast.makeText(getContext(), "Volunteer Button Is Click", Toast.LENGTH_SHORT).show();
//        }
//    }


}
