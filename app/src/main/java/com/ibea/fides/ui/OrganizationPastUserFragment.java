package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibea.fides.R;

/**
 * Created by N8Home on 1/31/17.
 */

public class OrganizationPastUserFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from organizationpastuserfragment.xml
        View view = inflater.inflate(R.layout.organizationpastuserfragment, container, false);
        return view;
    }
}
