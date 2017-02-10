package com.ibea.fides.ui;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.ibea.fides.utils.AdapterUpdateInterface;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsPendingForOrganizationFragment extends Fragment implements View.OnClickListener, AdapterUpdateInterface {
    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.button_CreateShift) Button mButton_CreateShift;

    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private Boolean isOrganization;

    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private AdapterUpdateInterface mThis;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbShiftsPendingForOrganizations = dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(mCurrentUser.getUid());

    public ShiftsPendingForOrganizationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View view){
        if(view == mButton_CreateShift){
            Intent intent = new Intent(this.getContext(), ShiftsCreateActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shifts_pending_for_organizations, container, false);
        ButterKnife.bind(this, view);

        mThis = this;

        isOrganization = PreferenceManager.getDefaultSharedPreferences(this.getContext()).getBoolean(Constants.KEY_ISORGANIZATION, false);

        dbRef.child(Constants.DB_NODE_USERS).child(mCurrentUser.getUid()).child("isOrganization").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isOrganization = dataSnapshot.getValue(Boolean.class);
                if(isOrganization){
                    setUpFirebaseAdapter();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mButton_CreateShift.setOnClickListener(this);
        setRecyclerViewItemTouchListener();
        return view;
    }

    // newInstance constructor for creating fragment with arguments
    public static ShiftsPendingForOrganizationFragment newInstance(int page, String title) {
        return new ShiftsPendingForOrganizationFragment();
    }

    @Override
    public void updateAdapter(){
        mFirebaseAdapter.notifyDataSetChanged();
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, FirebaseShiftViewHolder>
                (String.class, R.layout.new_shift_list_item, FirebaseShiftViewHolder.class, dbShiftsPendingForOrganizations) {

            @Override
            protected void populateViewHolder(final FirebaseShiftViewHolder viewHolder, final String shiftId, int position) {
                dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Shift shift = dataSnapshot.getValue(Shift.class);
                        viewHolder.bindShift(shift, "ShiftsPendingForOrg");
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

    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if(swipeDir == 4){
                    ((FirebaseShiftViewHolder) viewHolder).deleteShift(true);
                }else if(swipeDir == 8){
                    ((FirebaseShiftViewHolder) viewHolder).completeShift();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }


}
