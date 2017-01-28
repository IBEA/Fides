package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.utils.RecyclerItemListener;

import java.util.ArrayList;

/**
 * Created by KincaidJ on 1/28/17.
 */

public class DirtyFirebaseOrganizationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    Context mContext;
    Organization mOrg;
    RecyclerItemListener mTransfer;

    public DirtyFirebaseOrganizationViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindOrganization(String orgID, RecyclerItemListener transfer) {
        mTransfer = transfer;
        
        final TextView orgNameText = (TextView) mView.findViewById(R.id.nameText);
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_APPLICATIONS).child(orgID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Organization org = dataSnapshot.getValue(Organization.class);
                mOrg = org;
                orgNameText.setText(org.getOrgName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}