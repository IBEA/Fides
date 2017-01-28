package com.ibea.fides.ui;


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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseShiftViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsByZipcodeFragment extends Fragment {
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    public ShiftsByZipcodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shifts_by_zipcode, container, false);
        ButterKnife.bind(this, view);

        Log.v(">>>>", "In onCreateView");
        setUpFirebaseAdapter();
        return view;
    }

    // Store instance variables

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // newInstance constructor for creating fragment with arguments
    public static ShiftsByZipcodeFragment newInstance(int page, String title) {
        ShiftsByZipcodeFragment fragmentFirst = new ShiftsByZipcodeFragment();
        return fragmentFirst;
    }

    private void setUpFirebaseAdapter() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child("97201");

        //!! If statement to switch between zip and organization needed here. setUpFirebaseAdapter will need to take appropriate arguments. Defaulting to a zip code 97201 right now !!
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, DirtyFirebaseShiftViewHolder>
                (String.class, R.layout.dirty_shift_list_item, DirtyFirebaseShiftViewHolder.class, dbRef) {

            @Override
            protected void populateViewHolder(DirtyFirebaseShiftViewHolder viewHolder, String shiftID, int position) {
                viewHolder.bindShift(shiftID);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
