package com.ibea.fides.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by N8Home on 1/30/17.
 */

public class ProfileForOrganizationFragment extends Fragment {

    private String TAG = "ProfileForOrg";
    private Organization mOrganization;

    // image storage reference variables
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    StorageReference mImageRef;

    @Bind(R.id.imageView_orgPic) ImageView mOrgPic;
    @Bind(R.id.textView_orgName) TextView mOrgName;
    @Bind(R.id.textView_orgAddress) TextView mOrgAddress;
    @Bind(R.id.textView_orgAddressLineTwo) TextView mOrgAddressLineTwo;
    @Bind(R.id.textView_orgWebsite) TextView mOrgWebsite;
    @Bind(R.id.textView_orgDescription) TextView mOrgDescription;

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

        Intent intent = getActivity().getIntent();
        if(intent.hasExtra("organization")){
            Log.d(TAG, "Found intent");
            mOrganization = Parcels.unwrap(intent.getParcelableExtra("organization"));
        }else{
            Log.d(TAG, "Used bundle");
            mOrganization = Parcels.unwrap(getArguments().getParcelable("organization"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organization_profile, container, false);
        ButterKnife.bind(this, view);

        // assign image storage reference variables
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://fides-6faeb.appspot.com");
        mImageRef = mStorageRef.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

        mImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e("Garrett", "uri " + uri );
                Picasso.with(getActivity())
                        .load(uri)
                        .placeholder(R.drawable.avatar_blank)
                        .resize(450,400)
                        .centerCrop()
                        .into(mOrgPic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        mOrgName.setText(mOrganization.getName());
        mOrgAddress.setText(mOrganization.getStreetAddress());
        mOrgAddressLineTwo.setText(mOrganization.getCityAddress() + ", " + mOrganization.getStateAddress() + ", " + mOrganization.getZipcode());
        mOrgWebsite.setText(mOrganization.getUrl());
        mOrgDescription.setText(mOrganization.getDescription());

        return view;
    }



}
