package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Shift;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by Alaina Traxler on 1/25/2017.
 */

public class DirtyFirebaseShiftViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    Context mContext;

    public DirtyFirebaseShiftViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindShift(Shift shift) {
        TextView organizationTextView = (TextView) mView.findViewById(R.id.textView_Organization);
        TextView shortDescriptionTextView = (TextView) mView.findViewById(R.id.textView_ShortDescription);
        TextView zipCodeTextView = (TextView) mView.findViewById(R.id.textView_Zip);

        organizationTextView.setText(shift.getOrganizationName());
        shortDescriptionTextView.setText(shift.getShortDescription());
        zipCodeTextView.setText(String.valueOf(shift.getZip()));
    }

    @Override
    public void onClick(View view) {
        final ArrayList<Shift> shifts = new ArrayList<>();

        //This is going into the FULL, UNFILTERED shifts list. There should (will) ultimately be a way to point this toward the correct node of shiftsAvailable.
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_SHIFTS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    shifts.add(snapshot.getValue(Shift.class));
                }

                int itemPosition = getLayoutPosition();

                //Rough code for kicking into the pager adapter that it looks like you guys are using.
//                Intent intent = new Intent(mContext, ShiftDetailsActivity.class);
//                intent.putExtra("position", itemPosition + "");
//                intent.putExtra("shifts", Parcels.wrap(shifts));

//                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}