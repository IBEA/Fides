package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.ibea.fides.models.Shift;
import com.ibea.fides.ui.ShiftDetailsActivity;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alaina Traxler on 1/31/2017.
 */

public class FirebaseCompletedShiftViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private View mView;
    private Context mContext;
    private Shift mShift;

    public FirebaseCompletedShiftViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindShift(String shiftId, Boolean _isOrganization) {

        final TextView organizationTextView = (TextView) mView.findViewById(R.id.textView_Name);
        final TextView dateTextView = (TextView) mView.findViewById(R.id.textView_Date);
        final TextView descriptionTextView = (TextView) mView.findViewById(R.id.textView_ShortDescription);

        if(organizationTextView == null){
            Log.d(">>>>", "textView is null");
        }else{
            Log.d(">>>>", "textView is not null");
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_SHIFTS).child(shiftId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mShift = dataSnapshot.getValue(Shift.class);

                if(mShift != null){
                    organizationTextView.setText(mShift.getOrganizationName());
                    dateTextView.setText(mShift.getDate());
                    descriptionTextView.setText(mShift.getShortDescription());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        //!! Front end, this is where you pack the shift off to shift details !!
        Intent intent = new Intent(mContext, ShiftDetailsActivity.class);
        intent.putExtra("shift", Parcels.wrap(mShift));
        mContext.startActivity(intent);
    }


}

