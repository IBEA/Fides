package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;


/**
 * Created by KincaidJ on 1/28/17.
 */

public class DirtyFirebaseOrganizationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    Context mContext;
    Organization mOrganization;


    public DirtyFirebaseOrganizationViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindOrganization(Organization organization) {

        TextView nameText = (TextView) mView.findViewById(R.id.nameText);
        mOrganization = organization;
        nameText.setText(organization.getOrgName());
    }

    @Override
    public void onClick(View view) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_ORGANIZATIONS);
        ref.child(mOrganization.getPushID()).setValue(mOrganization);

        DatabaseReference oldRef = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_APPLICATIONS);
        oldRef.child(mOrganization.getPushID()).removeValue();

    }
}