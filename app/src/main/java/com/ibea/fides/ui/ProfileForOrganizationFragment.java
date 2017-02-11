package com.ibea.fides.ui;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by N8Home on 1/30/17.
 */

public class ProfileForOrganizationFragment extends Fragment {

    private Organization mOrganization;

    @Bind(R.id.imageView_orgPic) ImageView mOrgPic;
    @Bind(R.id.textView_orgName) TextView mOrgName;
    @Bind(R.id.textView_orgAddress) TextView mOrgAddress;
    @Bind(R.id.textView_orgAddressLineTwo) TextView mOrgAddressLineTwo;
    @Bind(R.id.textView_orgWebsite) TextView mOrgWebsite;
    @Bind(R.id.textView_orgDescription) TextView mOrgDescription;

    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public static ProfileForOrganizationFragment newInstance(Organization organization) {
        ProfileForOrganizationFragment fragment = new ProfileForOrganizationFragment();

        Log.d("Pass check: ", organization.getName());
        Bundle bundle = new Bundle();
        bundle.putParcelable("organization", Parcels.wrap(organization));
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOrganization = Parcels.unwrap(getArguments().getParcelable("organization"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organization_profile, container, false);
        ButterKnife.bind(this, view);

        mOrgName.setText(mOrganization.getName());
        mOrgAddress.setText(mOrganization.getStreetAddress());
        mOrgAddressLineTwo.setText(mOrganization.getCityAddress() + ", " + mOrganization.getStateAddress() + ", " + mOrganization.getZipcode());
        mOrgWebsite.setText(mOrganization.getUrl());
        mOrgDescription.setText(mOrganization.getDescription());

        return view;
    }


}
