package com.ibea.fides.ui;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.ibea.fides.adapters.NewShiftSearchAdapter;
import com.ibea.fides.models.Shift;
import com.ibea.fides.utils.AdapterUpdateInterface;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsPendingForVolunteerFragment extends Fragment implements AdapterUpdateInterface {
    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.textView_emptyList_Pending) TextView mEmptyWarning;

    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private Boolean isOrganization;

    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private AdapterUpdateInterface mThis;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbShiftsPendingForUser = dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(mCurrentUser.getUid());

    public ShiftsPendingForVolunteerFragment() {
        // Required empty public constructor
    }

    @Override
    public void updateAdapter(){
        mFirebaseAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shifts_pending_for_volunteer, container, false);
        ButterKnife.bind(this, view);

        mThis = this;
        Log.v(">>>>>", "ShiftsPending current user = " + mCurrentUser.getUid());
        Log.v(">>>>>", "In onCreateView for ShiftsPending");

        isOrganization = PreferenceManager.getDefaultSharedPreferences(this.getContext()).getBoolean(Constants.KEY_ISORGANIZATION, false);
        setUpFirebaseAdapter();

        setRecyclerViewItemTouchListener();

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
                (String.class, R.layout.list_item_shift_pending, FirebaseShiftViewHolder.class, dbShiftsPendingForUser) {

            @Override
            protected void populateViewHolder(final FirebaseShiftViewHolder viewHolder, final String shiftId, int position) {
                dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Shift shift = dataSnapshot.getValue(Shift.class);
                        viewHolder.bindShift(shift, "ShiftsPendingForVol");
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




        // Check whether the list is empty, and display message if so
//TODO: Deal with Asynchronicity issue, causing getItemCount() to always return 0
//        Toast.makeText(getActivity(), mFirebaseAdapter.getItemCount()+"", Toast.LENGTH_SHORT).show();
//
//        if(mFirebaseAdapter.getItemCount() > 0){
//            mEmptyWarning.setVisibility(View.INVISIBLE);
//            mRecyclerView.setVisibility(View.VISIBLE);
//        }
//        else {
//            mEmptyWarning.setVisibility(View.VISIBLE);
//            mRecyclerView.setVisibility(View.INVISIBLE);
//        }
    }

    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if(swipeDir == 4){
                    ((FirebaseShiftViewHolder) viewHolder).quitShift();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

}
