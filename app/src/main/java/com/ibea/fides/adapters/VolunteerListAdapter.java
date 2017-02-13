package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.User;
import com.ibea.fides.ui.MainActivity_Volunteer;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VolunteerListAdapter extends RecyclerView.Adapter<VolunteerListAdapter.VolunteerViewHolder> {
    private ArrayList<User> mVolunteers = new ArrayList<>();
    private User mVolunteer;

    public VolunteerListAdapter(Context context, ArrayList<User> volunteers) {
        mVolunteers = volunteers;
    }

    @Override
    public VolunteerListAdapter.VolunteerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_volunteer, parent, false);
        VolunteerViewHolder viewHolder = new VolunteerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VolunteerListAdapter.VolunteerViewHolder holder, int position) {
        holder.bindVolunteer(mVolunteers.get(position));
    }

    @Override
    public int getItemCount() {
        return mVolunteers.size();
    }

    public class VolunteerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Context mContext;
        @Bind(R.id.textView_Name) TextView mTextView_Name;

        public VolunteerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindVolunteer(User volunteer) {
            mVolunteer = volunteer;
            mTextView_Name.setText(mVolunteer.getName());
        }

        @Override
        public void onClick(View v){
            Intent intent = new Intent(mContext, MainActivity_Volunteer.class);
            intent.putExtra("user", Parcels.wrap(mVolunteer));
            Log.d(">>>>>", mVolunteer.getName());
            mContext.startActivity(intent);
        }

        public String getVolunteerId(){
            return mVolunteer.getPushId();
        }
    }
}