package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.adapters.FirebaseOrganizationViewHolder;
import com.ibea.fides.models.Organization;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AdminActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);
        setUpFirebaseAdapter();
    }

    @Override
    public void onClick(View view) {

    }

    private void setUpFirebaseAdapter() {
        FirebaseRecyclerAdapter mFirebaseAdapter = new FirebaseRecyclerAdapter<Organization, FirebaseOrganizationViewHolder>
                (Organization.class, R.layout.list_item_organization, FirebaseOrganizationViewHolder.class, dbPendingOrganizations) {

            @Override
            protected void populateViewHolder(FirebaseOrganizationViewHolder viewHolder, Organization org, int position) {
                viewHolder.bindOrganization(org);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}