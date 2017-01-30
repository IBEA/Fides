package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibea.fides.R;
import com.ibea.fides.models.Organization;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

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

    public class OrganizationViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        @Bind(R.id.nameText) TextView nameText;

        public OrganizationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindOrganization(Organization organization) {
            nameText.setText("Test");
        }
    }
}