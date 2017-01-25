package com.ibea.fides.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.adapters.OrganizationListAdapter;
import com.ibea.fides.models.Organization;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewOrganizationActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.organizationResultRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.emailInput) EditText mEmailInput;
    @Bind(R.id.nameInput) EditText mNameInput;
    @Bind(R.id.organizationInput) EditText mOrganizationNameInput;
    @Bind(R.id.zipcodeInput) EditText mZipcodeInput;
    @Bind(R.id.searchButton) Button mSearchButton;
    @Bind(R.id.submitButton) Button mSubmitButton;

    private OrganizationListAdapter mAdapter;
    public ArrayList<Organization> mOrganizations = new ArrayList<>();

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
            String name = mNameInput.getText().toString().trim();
            String zipcode = mZipcodeInput.getText().toString().trim();
            getOrganizations(name, zipcode);
        }

        if(view == mSubmitButton) {

        }

    }

    private void getOrganizations(String name, String zipcode) {
        // Temporary until API
        for(Organization org : mTestOrg) {
            if(org.getName().equals(name)) {
                mOrganizations.add(org);
            }
        }
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
}
