package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.utils.RecyclerItemListener;


/**
 * Created by KincaidJ on 1/28/17.
 */

public class DirtyFirebaseOrganizationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    Context mContext;

    public DirtyFirebaseOrganizationViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindOrganization(Organization organization) {
        TextView nameText = (TextView) mView.findViewById(R.id.nameText);

        nameText.setText(organization.getOrgName());
    }

    @Override
    public void onClick(View view) {
//        final ArrayList<Organization> organizations = new ArrayList<>();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_RESTAURANTS);
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    organizations.add(snapshot.getValue(Organization.class));
//                }
//
//                int itemPosition = getLayoutPosition();
//
//                Intent intent = new Intent(mContext, OrganizationDetailActivity.class);
//                intent.putExtra("position", itemPosition + "");
//                intent.putExtra("organizations", Parcels.wrap(organizations));
//
//                mContext.startActivity(intent);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
    }
}