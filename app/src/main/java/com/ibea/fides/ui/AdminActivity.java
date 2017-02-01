package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseOrganizationViewHolder;
import com.ibea.fides.models.Organization;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AdminActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;

    private FirebaseRecyclerAdapter mFirebaseAdapter;

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
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Organization, DirtyFirebaseOrganizationViewHolder>
                (Organization.class, R.layout.organization_list_item, DirtyFirebaseOrganizationViewHolder.class, dbPendingOrganizations) {

            @Override
            protected void populateViewHolder(DirtyFirebaseOrganizationViewHolder viewHolder, Organization org, int position) {
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