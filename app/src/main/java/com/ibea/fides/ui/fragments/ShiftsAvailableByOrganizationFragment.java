package com.ibea.fides.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.FirebaseShiftViewHolder;
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.Shift;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsAvailableByOrganizationFragment extends Fragment{
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.textView_Splash) TextView mTextView_Splash;

    static Organization mOrganization;

    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private Boolean lock = true;
    private String mCurrentUserId;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ShiftsAvailableByOrganizationFragment mThis;

    public ShiftsAvailableByOrganizationFragment() {
        // Required empty public constructor
    }

    public static ShiftsAvailableByOrganizationFragment newInstance(Organization organization) {
        mOrganization = organization;
        return new ShiftsAvailableByOrganizationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shifts_available_by_organization, container, false);
        ButterKnife.bind(this, view);
        String organizationId = mOrganization.getPushId();

        mThis = this;

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
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

        if(mAuth.getCurrentUser() != null){
            dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(mCurrentUserId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(!lock){
                        Log.d(">>>>>", "OnChildAdded");
                        mFirebaseAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    if(!lock){
                        Log.d(">>>>>", "OnChildRemoved");
                        mFirebaseAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            lock = false;

            setUpFirebaseAdapter();
        }

        return view;
    }
    private void setUpFirebaseAdapter() {
        String organizationID = mOrganization.getPushId();

        Log.d(">>>>>", "In SABO setup");
        Log.d(">SABO ORG ID>", organizationID);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, FirebaseShiftViewHolder>
                (String.class, R.layout.list_item_shift_pending, FirebaseShiftViewHolder.class, dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizationID)) {

            @Override
            protected void populateViewHolder(final FirebaseShiftViewHolder viewHolder, final String shiftId, int position) {
                Log.d(">>>>>", "In SABO");
                dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Shift shift = dataSnapshot.getValue(Shift.class);
                        viewHolder.bindShift(shift, "ShiftsByOrganization");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
