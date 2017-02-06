package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.User;

/**
 * Created by N8Home on 1/30/17.
 */

public class ProfileForOrganizationFragment extends Fragment {
    private static Organization mOrganization;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.organizationprofilefragment, container, false);

        return view;
    }

    // newInstance constructor for creating fragment with arguments
    public static ProfileForOrganizationFragment newInstance(Organization organization) {
        mOrganization = organization;
        ProfileForOrganizationFragment fragment = new ProfileForOrganizationFragment();
        return fragment;
    }


}
