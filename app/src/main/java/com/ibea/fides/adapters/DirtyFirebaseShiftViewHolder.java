package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
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
//        ImageView restaurantImageView = (ImageView) mView.findViewById(R.id.restaurantImageView);
//        TextView nameTextView = (TextView) mView.findViewById(R.id.restaurantNameTextView);
//        TextView categoryTextView = (TextView) mView.findViewById(R.id.categoryTextView);
//        TextView ratingTextView = (TextView) mView.findViewById(R.id.ratingTextView);

//        nameTextView.setText(restaurant.getName());

    }

    @Override
    public void onClick(View view) {
        final ArrayList<Shift> shifts = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_SHIFTS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    shifts.add(snapshot.getValue(Shift.class));
                }

                int itemPosition = getLayoutPosition();

//                Intent intent = new Intent(mContext, RestaurantDetailActivity.class);
//                intent.putExtra("position", itemPosition + "");
//                intent.putExtra("restaurants", Parcels.wrap(restaurants));

//                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}