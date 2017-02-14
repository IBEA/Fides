package com.ibea.fides.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.FirebaseShiftViewHolder;
import com.ibea.fides.models.Shift;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftsCompletedForOrganizationFragment extends Fragment {
    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.textView_Splash) TextView mTextView_Splash;

    FirebaseRecyclerAdapter mFirebaseAdapter;
    Boolean isOrganization;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String mUserId;


    public ShiftsCompletedForOrganizationFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static ShiftsCompletedForOrganizationFragment newInstance(int page, String title) {
        return new ShiftsCompletedForOrganizationFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shifts_completed_for_organization, container, false);
        ButterKnife.bind(this, view);

        final String currentUserId;
        FirebaseAuth auth = FirebaseAuth.getInstance();


        if(auth.getCurrentUser() != null){
            mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            dbRef.child(Constants.DB_NODE_USERS).child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    isOrganization = dataSnapshot.child("isOrganization").getValue(Boolean.class);
                    setUpFirebaseAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        FirebaseDatabase.getInstance().getReference().child(Constants.DB_NODE_SHIFTSCOMPLETE).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    mTextView_Splash.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    mTextView_Splash.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void setUpFirebaseAdapter() {
        DatabaseReference dbFirebaseNode;

        dbFirebaseNode = FirebaseDatabase.getInstance().getReference().child(Constants.DB_NODE_SHIFTSCOMPLETE).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(mUserId);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, FirebaseShiftViewHolder>
                (String.class, R.layout.list_item_shift, FirebaseShiftViewHolder.class, dbFirebaseNode) {

            @Override
            protected void populateViewHolder(final FirebaseShiftViewHolder viewHolder, String shiftId, int position) {
                dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Shift shift = dataSnapshot.getValue(Shift.class);
                        viewHolder.bindShift(shift, "ShiftsCompletedForVolunteer");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }
}