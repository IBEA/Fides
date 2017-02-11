package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.ui.MainActivity_Organization;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrganizationListAdapter extends RecyclerView.Adapter<OrganizationListAdapter.OrganizationViewHolder> {
    private ArrayList<Organization> mOrganizations = new ArrayList<>();
    private Organization mOrganization;

    public OrganizationListAdapter(Context context, ArrayList<Organization> organizations) {
        mOrganizations = organizations;
    }

    @Override
    public OrganizationListAdapter.OrganizationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_organization, parent, false);
        OrganizationViewHolder viewHolder = new OrganizationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OrganizationListAdapter.OrganizationViewHolder holder, int position) {
        holder.bindOrganization(mOrganizations.get(position));
    }

    @Override
    public int getItemCount() {
        return mOrganizations.size();
    }

    public class OrganizationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Context mContext;
        @Bind(R.id.nameText) TextView nameText;

        public OrganizationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindOrganization(Organization organization) {
            mOrganization = organization;
            nameText.setText(organization.getName());
        }

        @Override
        public void onClick(View v){
            Intent intent = new Intent(mContext, MainActivity_Organization.class);
            intent.putExtra("organization", Parcels.wrap(mOrganization));
            mContext.startActivity(intent);
        }
    }
}