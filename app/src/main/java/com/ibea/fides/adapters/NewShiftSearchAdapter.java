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
import com.ibea.fides.models.Shift;
import com.ibea.fides.ui.MainActivity_Organization;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alaina Traxler on 2/7/2017.
 */

public class NewShiftSearchAdapter extends RecyclerView.Adapter<NewShiftSearchAdapter.NewShiftSearchViewHolder> {
    private ArrayList<Shift> mShifts = new ArrayList<>();
    private Shift mShift;
    private final String TAG = "NewShiftSearchAdapter";

    public NewShiftSearchAdapter(Context context, ArrayList<Shift> shifts) {
            mShifts = shifts;
            }

    @Override
    public NewShiftSearchAdapter.NewShiftSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_shift_list_item, parent, false);
            NewShiftSearchViewHolder viewHolder = new NewShiftSearchViewHolder(view);
            return viewHolder;
            }

    @Override
    public void onBindViewHolder(NewShiftSearchAdapter.NewShiftSearchViewHolder holder, int position) {
            holder.bindShift(mShifts.get(position));
            }

    @Override
    public int getItemCount() {
            return mShifts.size();
            }

    public class NewShiftSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.textView_OrganizationName) TextView mTextView_OrganizationName;
        private Context mContext;

        public NewShiftSearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindShift(Shift shift) {
            mShift = shift;
            mTextView_OrganizationName.setText(mShift.getOrganizationName());
        }

        @Override
        public void onClick(View v){
            //TODO: Parcel mShift and send to shift details
        }
    }
}