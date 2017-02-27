package com.ibea.fides.ui.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsCompletedForVolunteerFragment extends Fragment {
    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.textView_Splash) TextView mTextView_Splash;
    private ValueEventListener mListener;
    FirebaseRecyclerAdapter mFirebaseAdapter;
    DatabaseReference dbShiftsCompletedForVolunteer;
    String mUserId;

    public ShiftsCompletedForVolunteerFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static ShiftsCompletedForVolunteerFragment newInstance(int page, String title) {
        ShiftsCompletedForVolunteerFragment fragmentFirst = new ShiftsCompletedForVolunteerFragment();
        Log.v("<<<<<", "ShiftsCompleted newInstance");
        return fragmentFirst;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shifts_completed_for_volunteers, container, false);
        ButterKnife.bind(this, view);
        Log.v("<<<<<", "In onCreateView for Completed");

        this.setHasOptionsMenu(true);



        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbShiftsCompletedForVolunteer = FirebaseDatabase.getInstance().getReference().child(Constants.DB_NODE_SHIFTSCOMPLETE).child(Constants.DB_SUBNODE_VOLUNTEERS).child(mUserId);

        mListener = dbShiftsCompletedForVolunteer.addValueEventListener(new ValueEventListener() {
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

        setUpFirebaseAdapter();
        return view;
    }

    private void setUpFirebaseAdapter() {
        Log.v(">>>>>", "in CompletedSetup");
        DatabaseReference dbFirebaseNode;

        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, FirebaseShiftViewHolder>
                (String.class, R.layout.list_item_shift, FirebaseShiftViewHolder.class, dbShiftsCompletedForVolunteer) {

            @Override
            protected void populateViewHolder(final FirebaseShiftViewHolder viewHolder, String shiftId, int position) {
                FirebaseDatabase.getInstance().getReference().child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_volunteertutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//          TODO: Set up an actual message
            builder.setMessage("This is the Shifts Completed Page. A list of your completed shifts can be found here. Click on them for more information.");

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });


            AlertDialog dialog = builder.create();

            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mListener != null) {
            dbShiftsCompletedForVolunteer.removeEventListener(mListener);

        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mListener != null) {
            dbShiftsCompletedForVolunteer.removeEventListener(mListener);
        }

    }

}
