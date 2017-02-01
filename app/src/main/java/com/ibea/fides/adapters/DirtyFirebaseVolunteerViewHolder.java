package com.ibea.fides.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.User;


/**
 * Created by KincaidJ on 1/31/17.
 */

public class DirtyFirebaseVolunteerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    Context mContext;
    User mUser;
    Button mSubmitButton;

    public DirtyFirebaseVolunteerViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();

    }

    public void bindUser(String userId) {
        final TextView userName = (TextView) mView.findViewById(R.id.textView_Name);
        final RadioButton badRadio = (RadioButton) mView.findViewById(R.id.badRadio);
        final RadioButton poorRadio = (RadioButton) mView.findViewById(R.id.poorRadio);
        final RadioButton goodRadio = (RadioButton) mView.findViewById(R.id.goodRadio);
        final RadioButton greatRadio = (RadioButton) mView.findViewById(R.id.greatRadio);
        mSubmitButton = (Button) mView.findViewById(R.id.buttonSubmit);

        mSubmitButton.setOnClickListener(this);

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
        if(view == mSubmitButton) {
            Log.d("Justin", "WOOP");
        }
    }
}
