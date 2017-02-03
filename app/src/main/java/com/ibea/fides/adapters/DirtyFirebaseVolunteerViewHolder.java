package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Shift;
import com.ibea.fides.models.User;

import java.util.List;


/**
 * Created by KincaidJ on 1/31/17.
 */

public class DirtyFirebaseVolunteerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    Context mContext;
    User mUser;
    Button mDislikeButton;
    Button mLikeButton;

    // Rating System
    final int DISLIKE = 0;
    final int LIKE = 3;

    String shiftId;
    String indexKey;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public DirtyFirebaseVolunteerViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();

    }

    public void bindUser(String userId, String _shiftId, int position, boolean rated) {
        final TextView userName = (TextView) mView.findViewById(R.id.textView_Name);
        mDislikeButton = (Button) mView.findViewById(R.id.dislikeButton);
        mLikeButton = (Button) mView.findViewById(R.id.likeButton);

        mDislikeButton.setOnClickListener(this);
        mLikeButton.setOnClickListener(this);


        if(rated) {
            mLikeButton.setVisibility(View.GONE);
            mDislikeButton.setVisibility(View.GONE);
        }

        shiftId = _shiftId;
        indexKey = Integer.toString(position);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_USERS).child(userId);
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUser = user;
                userName.setText(mUser.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == mDislikeButton) {
            rate(DISLIKE);
        } else if(view == mLikeButton) {
            rate(LIKE);
        }
    }

    public void rate(int rating) {
        mDislikeButton.setVisibility(View.GONE);
        mLikeButton.setVisibility(View.GONE);


        List<Integer> ratingHistory = mUser.getRatingHistory();
        int size = ratingHistory.size();
        ratingHistory.add(rating);
        int modifiedRating = 0;
        int modifiedMax = 0;
        int index = 1;
        int modifier = 0;

        for(Integer rate : ratingHistory) {
            modifier = index/size;
            modifiedRating += (modifier * rate);
            modifiedMax += (modifier * LIKE);
            ++index;
        }

        int finalRating = (modifiedRating/modifiedMax) * 100 ;

        mUser.setRating(finalRating);
        mUser.setRatingHistory(ratingHistory);


        dbRef.child(Constants.DB_NODE_USERS).child(mUser.getPushId()).setValue(mUser);

        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("currentVolunteers").child(indexKey).removeValue();

        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Shift shift = dataSnapshot.getValue(Shift.class);

                // AJ VALIDATION
                // If you remove the following code and instead manipulate the shift with .removeVolunteers and push it back
                // up, then you should encounter the out of bounds error.. I think.

                shift.getCurrentVolunteers().remove(mUser.getPushId());
                shift.addRated(mUser.getPushId());
                dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("currentVolunteers").setValue(shift.getCurrentVolunteers());
                dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("ratedVolunteers").setValue(shift.getRatedVolunteers());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
