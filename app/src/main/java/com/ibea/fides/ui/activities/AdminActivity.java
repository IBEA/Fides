package com.ibea.fides.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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
        setRecyclerViewItemTouchListener();
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

    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Log.d("Swipe dir: ", String.valueOf(swipeDir));

                if(swipeDir == 8){
                    ((FirebaseOrganizationViewHolder) viewHolder).approveOrg();
                }else if(swipeDir == 4){
                    ((FirebaseOrganizationViewHolder) viewHolder).rejectOrg();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}