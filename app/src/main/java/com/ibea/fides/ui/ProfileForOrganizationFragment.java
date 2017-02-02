package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by N8Home on 1/30/17.
 */

public class ProfileForOrganizationFragment extends Fragment {

    private static Organization mOrganization;

    @Bind(R.id.organizationNameTextView) TextView username;
    @Bind(R.id.OrganizationContact) TextView organizationContact;
    @Bind(R.id.OrganizationAbout) TextView mBlurb;

    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.organizationprofilefragment, container, false);
        ButterKnife.bind(this, view);

        /*
        dbRef.child(Constants.DB_NODE_ORGANIZATIONS).child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username.setText( (String) dataSnapshot.child("name").getValue());
                mBlurb.setText( (String) dataSnapshot.child("description").getValue());
                organizationContact.setText( (String) dataSnapshot.child("address").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); */

        username.setText(mOrganization.getName());
        mBlurb.setText(mOrganization.getDescription());
        organizationContact.setText(mOrganization.getAddress());

        return view;
    }

    public static ProfileForOrganizationFragment newInstance(Organization organization) {
        mOrganization = organization;
        Log.d(">>>>>", mOrganization.getName());
        Log.d(">>>>>", "Description " + mOrganization.getDescription());
        Log.d(">>>>>", "Address " +mOrganization.getAddress());
        ProfileForOrganizationFragment fragment = new ProfileForOrganizationFragment();
        return fragment;
    }

}
