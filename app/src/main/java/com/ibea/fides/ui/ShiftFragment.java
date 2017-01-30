package com.ibea.fides.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.ibea.fides.R.layout.shift_fragment;

/**
 * Created by N8Home on 1/27/17.
 */

public class ShiftFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.organizationName) TextView mOrganizationName;
    @Bind(R.id.organizationContact) TextView mOrganizationContact;
    @Bind(R.id.organizationShiftDetail) TextView mOrganizationShiftDeatil;
    @Bind(R.id.organizationEditButton) Button mOrganizationEditButton;
    @Bind(R.id.organizationDeleteButton) Button mOrganizationDeleteButton;
    @Bind(R.id.organizationNumberOfVolunteer) TextView mOrganinzationNumberOfVolunteer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         Get the view from shift_fragment.xml
        View view = inflater.inflate(shift_fragment, container, false);
        ButterKnife.bind(this, view);

        mOrganizationName.setOnClickListener(this);
        mOrganizationContact.setOnClickListener(this);
        mOrganizationEditButton.setOnClickListener(this);
        mOrganizationDeleteButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == mOrganizationName) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.oregonhumane.org/"));
            startActivity(webIntent);
        }
//        if (v == mOrganizationContact) {
//            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("geo:" + mOrgnaization.getLatitude()
//                            + "," + mOrgnaization.getLongitude()
//                            + "?q=(" + mOrgnaization.getName() + ")"));
//            startActivity(mapIntent);
//        }
        if (v == mOrganizationEditButton){
            Toast.makeText(getContext(), "Edit Button Is Press", Toast.LENGTH_SHORT).show();
        }
        if (v == mOrganizationDeleteButton){
            Toast.makeText(getContext(), "Delete Button Is Press", Toast.LENGTH_SHORT).show();
        }
    }
}
