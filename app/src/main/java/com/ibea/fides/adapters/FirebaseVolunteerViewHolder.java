package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
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

public class FirebaseVolunteerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private View mView;
    private User mUser;
    private Button mDislikeButton;
    private Button mLikeButton;

    // Rating System
    final private int DISLIKE = 0;
    final private int LIKE = 3;

    private  String shiftId;
    private String indexKey;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public FirebaseVolunteerViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
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
                mUser = dataSnapshot.getValue(User.class);
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

    private void rate(int rating) {
        mDislikeButton.setVisibility(View.GONE);
        mLikeButton.setVisibility(View.GONE);


        List<Integer> ratingHistory = mUser.getRatingHistory();
        ratingHistory.add(rating);
        float size = ratingHistory.size();
        float modifiedRating = 0;
        float modifiedMax = 0;
        float index = 1;
        float modifier = 0;

        for(Integer rate : ratingHistory) {
            Log.d("Justin Index: ", index + "");
            Log.d("Justin Rate on Index: ", rate + "");
            Log.d("Justin modifiedrating: ", modifiedRating + "");
            Log.d("Justin modifiedMax: ", modifiedMax + "");
            Log.d("Justin Size: ", size + "");
            modifier = index/size;
            modifiedRating += (modifier * rate);
            modifiedMax += (modifier * LIKE);
            ++index;
            Log.d("Justin modifier: ", modifier + "");
        }

        Log.d("Justin FIndex: ", index + "");
        Log.d("Justi Fmodifiedrating: ", modifiedRating + "");
        Log.d("Justin FmodifiedMax: ", modifiedMax + "");
        float finalFloatRating = (modifiedRating/modifiedMax) * 100 ;
        Log.d("Justin finalRating: ", finalFloatRating + "");
        int finalRating = Math.round(finalFloatRating);
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
