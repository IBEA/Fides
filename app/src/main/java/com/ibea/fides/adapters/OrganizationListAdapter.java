package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.ui.OrganizationApplicationActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by KincaidJ on 1/25/17.
 */

public class OrganizationListAdapter extends RecyclerView.Adapter<OrganizationListAdapter.OrganizationViewHolder> {
    private ArrayList<Organization> mOrganizations = new ArrayList<>();
    private Context mContext;

    public OrganizationListAdapter(Context context, ArrayList<Organization> organizations) {
        mContext = context;
        mOrganizations = organizations;
    }

    @Override
    public OrganizationListAdapter.OrganizationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organization_list_item, parent, false);
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
        @Bind(R.id.nameText) TextView mOrganizationName;
        private Context mContext;

        public OrganizationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindOrganization(Organization organization) {
            mOrganizationName.setText(organization.getName());
        }

        @Override
        public void onClick(View v) {
            ((OrganizationApplicationActivity) mContext).userItemClick(getAdapterPosition(), "unspecified");
        }
    }
}
