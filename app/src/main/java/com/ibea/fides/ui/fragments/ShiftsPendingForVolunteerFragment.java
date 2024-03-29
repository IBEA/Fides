package com.ibea.fides.ui.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.ibea.fides.ui.activities.SearchActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsPendingForVolunteerFragment extends Fragment implements View.OnClickListener{
    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.textView_Splash) TextView mTextView_Splash;
    @Bind(R.id.button_FindShift) FloatingActionButton mButton_Search;

    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private ValueEventListener mListener;

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

        mListener = dbShiftsPendingForUser.addValueEventListener(new ValueEventListener() {
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

        Log.v(">>>>>", "ShiftsPending current user = " + mCurrentUser.getUid());
        Log.v(">>>>>", "In onCreateView for ShiftsPending");

        setUpFirebaseAdapter();

        setRecyclerViewItemTouchListener();
        mButton_Search.setOnClickListener(this);

        return view;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

    }

    @Override
    public void onClick(View view) {
        if(view == mButton_Search){
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        }
    }

    // newInstance constructor for creating fragment with arguments
    public static ShiftsPendingForVolunteerFragment newInstance(int page, String title) {
        ShiftsPendingForVolunteerFragment fragmentFirst = new ShiftsPendingForVolunteerFragment();
        return fragmentFirst;
    }

    private void setUpFirebaseAdapter() {

        Log.v(">>>>>", "In setupFirebaseAdapter for ShiftsPendingUsers");

        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, FirebaseShiftViewHolder>
                (String.class, R.layout.list_item_shift, FirebaseShiftViewHolder.class, dbShiftsPendingForUser) {

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
    }

    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if(swipeDir == 4){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Cancel participation?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            ((FirebaseShiftViewHolder) viewHolder).quitShift();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            mFirebaseAdapter.notifyDataSetChanged();
                        }
                    });

                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });


                    AlertDialog dialog = builder.create();

                    dialog.show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_volunteertutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//          TODO: Set up an actual message
            builder.setMessage("This is the Volunteer Shifts Pending Page");

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
            dbShiftsPendingForUser.removeEventListener(mListener);

        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mListener != null) {
            dbShiftsPendingForUser.removeEventListener(mListener);
        }

    }

}
