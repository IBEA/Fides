package com.ibea.fides.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.adapters.OrganizationListAdapter;
import com.ibea.fides.models.Organization;
import com.ibea.fides.utils.RecyclerItemListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewOrganizationActivity extends BaseActivity implements View.OnClickListener, RecyclerItemListener {

    @Bind(R.id.organizationResultRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.nameInput) EditText mNameInput;
    @Bind(R.id.organizationInput) EditText mOrganizationNameInput;
    @Bind(R.id.addressInput) EditText mAddressInput;
    @Bind(R.id.searchButton) Button mSearchButton;
    @Bind(R.id.submitButton) Button mSubmitButton;

    private OrganizationListAdapter mAdapter;
    public ArrayList<Organization> mOrganizations = new ArrayList<>();
    private String organizationId;

    // Temporary List
    public ArrayList<Organization> mTestOrg = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_organization);
        ButterKnife.bind(this);

        // Temporary premade list of organizations
        Organization org1 = new Organization("1", "Good Sam");
        Organization org2 = new Organization("2", "Legacy");
        Organization org3 = new Organization("3", "Meals on Wheels");
        Organization org4 = new Organization("4", "Hack Oregon");
        Organization org5 = new Organization("5", "Blankets All Around");
        Organization org6 = new Organization("6", "Black Lives Matter");
        Organization org7 = new Organization("7", "OHSU");
        mTestOrg.add(org1);
        mTestOrg.add(org2);
        mTestOrg.add(org3);
        mTestOrg.add(org4);
        mTestOrg.add(org5);
        mTestOrg.add(org6);
        mTestOrg.add(org7);

        // Set Click Listener
        mSubmitButton.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Will eventually call API - Temporary comparison to premade list of organizations
        if(view == mSearchButton) {
            String orgName = mOrganizationNameInput.getText().toString().trim();
            String address = mAddressInput.getText().toString().trim();
            getOrganizations(orgName, address);
        }

        // Sends email to us claiming an organization
        if(view == mSubmitButton) {
            String orgName = mOrganizationNameInput.getText().toString().trim();
            String userName = mNameInput.getText().toString().trim();
            claimOrganization(userName, organizationId, orgName);
        }

    }

    // Populate RecyclerView with all matching organizations that have not been claimed
    private void getOrganizations(String orgName, String address) {
        // Temporary until API - Populate Array List with matching organizations
        for(Organization org : mTestOrg) {
            if(org.getName().equals(orgName)) {
                mOrganizations.add(org);
            }
        }

        // Consult Database to see if organizations have been claimed, then run adapter
        dbOrganizations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(Organization org : mOrganizations) {
                    if(dataSnapshot.hasChild(org.getName())) {
                       mOrganizations.remove(org);
                    }
                }

                // Populate recyclerview
                NewOrganizationActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mAdapter = new OrganizationListAdapter(getApplicationContext(), mOrganizations);
                        mRecyclerView.setAdapter(mAdapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(NewOrganizationActivity.this);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setHasFixedSize(true);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Interface function that allows information regarding an item in a RecyclerView to be accessed directly
    @Override
    public void userItemClick(int pos) {
        organizationId = mOrganizations.get(pos).getName();
    }

    // Populate email to us upon claim request
    private void claimOrganization(String userName, String organizationId, String organizationName) {
        String[] TO = {"justin.m.kincaid.work@gmail.com"};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, organizationName + " Requests Access");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
