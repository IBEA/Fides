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
import com.ibea.fides.models.User;

import java.util.List;


/**
 * Created by KincaidJ on 1/31/17.
 */

public class DirtyFirebaseVolunteerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    Context mContext;
    User mUser;
    Button mBadButton;
    Button mPoorButton;
    Button mGoodButton;
    Button mGreatButton;
    int bad = -5;
    int poor = -2;
    int good = 2;
    int great = 4;
    int base = 2;


    public DirtyFirebaseVolunteerViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();

    }

    public void bindUser(String userId) {
        final TextView userName = (TextView) mView.findViewById(R.id.textView_Name);

        mBadButton = (Button) mView.findViewById(R.id.badButton);
        mPoorButton = (Button) mView.findViewById(R.id.poorButton);
        mGoodButton = (Button) mView.findViewById(R.id.goodButton);
        mGreatButton = (Button) mView.findViewById(R.id.greatButton);

        mBadButton.setOnClickListener(this);
        mPoorButton.setOnClickListener(this);
        mGoodButton.setOnClickListener(this);
        mGreatButton.setOnClickListener(this);


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
        if(view == mBadButton) {
            rate(bad);
        } else if(view == mPoorButton) {
            rate(poor);
        } else if(view == mGoodButton) {
            rate(good);
        } else if(view == mGreatButton) {
            rate(great);
        }
    }

    public void rate(int rating) {
        mBadButton.setVisibility(View.GONE);
        mPoorButton.setVisibility(View.GONE);
        mGoodButton.setVisibility(View.GONE);
        mGreatButton.setVisibility(View.GONE);

        List<Integer> ranking = mUser.getRanking();

        if(ranking.size() > 0) {
            ranking.set(0, ranking.get(0) + rating);
            ranking.set(1, ranking.get(1) + base);
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();


        dbRef.child(Constants.DB_NODE_USERS).child(mUser.getPushId()).setValue(mUser);
    }
}
