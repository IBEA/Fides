package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.ui.activities.OrganizationProfileActivity;
import com.ibea.fides.ui.fragments.ProfileForOrganizationFragment;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class OrganizationListAdapter extends RecyclerView.Adapter<OrganizationListAdapter.OrganizationViewHolder> {
    private ArrayList<Organization> mOrganizations = new ArrayList<>();
    private Organization mOrganization;

    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    StorageReference mImageRef;

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
        @Bind(R.id.zipcodeText) TextView zipText;
        @Bind(R.id.imageView_orgThumbnail) ImageView orgThumnail;

        public OrganizationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindOrganization(Organization organization) {
            mOrganization = organization;
            nameText.setText(organization.getName());
            zipText.setText(organization.getZipcode());

            String orgId = mOrganization.getPushId();
            // assign image storage reference variables
            mStorage = FirebaseStorage.getInstance();
            mStorageRef = mStorage.getReferenceFromUrl("gs://fides-6faeb.appspot.com");
            mImageRef = mStorageRef.child("images/" + orgId + ".jpg");

            mImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(mContext)
                            .load(uri)
                            .placeholder(R.drawable.avatar_blank)
                            .resize(450,400)
                            .into(orgThumnail);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

        @Override
        public void onClick(View v){
            Intent intent = new Intent(mContext, OrganizationProfileActivity.class);
            intent.putExtra("organization", Parcels.wrap(mOrganization));
            mContext.startActivity(intent);
        }
    }
}