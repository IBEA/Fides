package com.ibea.fides.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseShiftViewHolder;
import com.ibea.fides.adapters.FirebaseCompletedShiftViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsCompletedForVolunteersFragment extends Fragment {
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    FirebaseRecyclerAdapter mFirebaseAdapter;
    Boolean isOrganization;


    public ShiftsCompletedForVolunteersFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static ShiftsCompletedForVolunteersFragment newInstance(int page, String title) {
        ShiftsCompletedForVolunteersFragment fragmentFirst = new ShiftsCompletedForVolunteersFragment();
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
        setUpFirebaseAdapter();
        return view;
    }

    private void setUpFirebaseAdapter() {
        Log.v(">>>>>", "in CompletedSetup");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Constants.DB_NODE_SHIFTSCOMPLETE).child(Constants.DB_SUBNODE_VOLUNTEERS).child(currentUserId);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, FirebaseCompletedShiftViewHolder>
                (String.class, R.layout.completed_shift_list_item, FirebaseCompletedShiftViewHolder.class, dbRef) {

            @Override
            protected void populateViewHolder(FirebaseCompletedShiftViewHolder viewHolder, String shiftId, int position) {
                viewHolder.bindShift(shiftId, true);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
