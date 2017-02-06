package com.ibea.fides.ui;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsPendingForVolunteerFragment extends Fragment {
    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;

    private FirebaseRecyclerAdapter mFirebaseAdapter;

    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private Boolean isOrganization;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbShiftsPendingForUser = dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(mCurrentUser.getUid());

    public ShiftsPendingForVolunteerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shifts_pending_for_volunteer, container, false);
        ButterKnife.bind(this, view);

        Log.v(">>>>>", "ShiftsPending current user = " + mCurrentUser.getUid());
        Log.v(">>>>>", "In onCreateView for ShiftsPending");

        isOrganization = PreferenceManager.getDefaultSharedPreferences(this.getContext()).getBoolean(Constants.KEY_ISORGANIZATION, false);
        setUpFirebaseAdapter();

        return view;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // newInstance constructor for creating fragment with arguments
    public static ShiftsPendingForVolunteerFragment newInstance(int page, String title) {
        ShiftsPendingForVolunteerFragment fragmentFirst = new ShiftsPendingForVolunteerFragment();
        return fragmentFirst;
    }

    private void setUpFirebaseAdapter() {

        Log.v(">>>>>", "In setupFirebaseAdapter for ShiftsPendingUsers");

        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, FirebaseShiftViewHolder>
                (String.class, R.layout.dirty_shift_list_item, FirebaseShiftViewHolder.class, dbShiftsPendingForUser) {

            @Override
            protected void populateViewHolder(final FirebaseShiftViewHolder viewHolder, final String shiftId, int position) {
                dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Shift shift = dataSnapshot.getValue(Shift.class);
                        viewHolder.bindShift(shift, isOrganization, "ShiftsPendingForVol");
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
