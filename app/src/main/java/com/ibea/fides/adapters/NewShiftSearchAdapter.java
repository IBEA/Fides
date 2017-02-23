package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ibea.fides.R;
import com.ibea.fides.models.Shift;
import com.ibea.fides.ui.activities.ShiftDetailsActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alaina Traxler on 2/7/2017.
 */

public class NewShiftSearchAdapter extends RecyclerView.Adapter<NewShiftSearchAdapter.NewShiftSearchViewHolder> {
    private ArrayList<Shift> mShifts = new ArrayList<>();
    private String mUserId;
    private final String TAG = "NewShiftSearchAdapter";

    public NewShiftSearchAdapter(Context context, ArrayList<Shift> shifts) {
            mShifts = shifts;

            mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }

    @Override
    public NewShiftSearchAdapter.NewShiftSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shift, parent, false);
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
        @Bind(R.id.textView_ShortDesc) TextView mTextView_ShortDesc;
        @Bind(R.id.textView_StartTime) TextView mTextView_StartTime;
        @Bind(R.id.textView_EndTime) TextView mTextView_EndTime;
        @Bind(R.id.textView_StartDate) TextView mTextView_StartDate;

        private Context mContext;

        public NewShiftSearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindShift(Shift shift) {
            mTextView_OrganizationName.setText(shift.getOrganizationName());
            mTextView_ShortDesc.setText(shift.getShortDescription());
            mTextView_StartTime.setText(shift.getStartTime());
            mTextView_EndTime.setText(shift.getEndTime());
            mTextView_StartDate.setText(shift.getStartDate());
        }

        @Override
        public void onClick(View v){
            Intent intent = new Intent(mContext, ShiftDetailsActivity.class);
            intent.putExtra("shiftId", mShifts.get(this.getAdapterPosition()).getPushId());
            mContext.startActivity(intent);
        }

    }
}