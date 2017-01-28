package com.ibea.fides.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseOrganizationViewHolder;
import com.ibea.fides.adapters.DirtyFirebaseShiftViewHolder;
import com.ibea.fides.models.Organization;
import com.ibea.fides.utils.RecyclerItemListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AdminActivity extends BaseActivity implements View.OnClickListener, RecyclerItemListener {
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerItemListener mTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

        mTransfer = this;
        setUpFirebaseAdapter();
    }

    @Override
    public void userItemClick(Object data, String view){

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