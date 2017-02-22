package com.ibea.fides.ui.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.NewShiftSearchAdapter;
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.Shift;
import com.ibea.fides.models.Volunteer;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsAvailableByOrganizationFragment extends Fragment {
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.textView_Splash) TextView mTextView_Splash;

    static Organization mOrganization;
    private Volunteer mVolunteer;
    static boolean isVolunteer;
    private NewShiftSearchAdapter mAdapter;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private Boolean lock = true;
    private String mCurrentUserId;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ShiftsAvailableByOrganizationFragment mThis;



    private Context mContext;
    private RecyclerView.Adapter mRecyclerAdapter;
    private ArrayList<Shift> shifts = new ArrayList<>();
    private String userId;
    private Boolean foundResults = false;



    public ShiftsAvailableByOrganizationFragment() {
        // Required empty public constructor
    }

    public static ShiftsAvailableByOrganizationFragment newInstance(Organization organization, boolean isOrg) {
        mOrganization = organization;
        isVolunteer = !isOrg;
        return new ShiftsAvailableByOrganizationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        mOrganization = Parcels.unwrap(intent.getParcelableExtra("organization"));
        this.setHasOptionsMenu(true);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shifts_available_by_organization, container, false);
        ButterKnife.bind(this, view);
        String organizationId = mOrganization.getPushId();
        mContext = this.getContext();

        mAdapter = new NewShiftSearchAdapter(mContext, shifts);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        setRecyclerViewItemTouchListener();

        mThis = this;

        this.setHasOptionsMenu(true);
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(isVolunteer) {
            getVolunteer();
        }


        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(mOrganization.getPushId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0){
                    mTextView_Splash.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    mTextView_Splash.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("Justin", snapshot.getKey());
                    fetchShift(snapshot.getKey());
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
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    if(!lock){
                        Log.d(">>>>>", "OnChildRemoved");
                        mAdapter.notifyDataSetChanged();
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
        }

        fetchShiftIds(mOrganization.getCityAddress(), mOrganization.getStateAddress());

        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_organizationtutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//          TODO: Set up an actual message
            builder.setMessage("This is the Shifts Available Page");

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Log.d("Justin", "Dismiss");
                }
            });


            AlertDialog dialog = builder.create();

            dialog.show();

        } else if (id == R.id.action_volunteertutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//          TODO: Set up an actual message
            builder.setMessage("This is the Shifts Available Page. Swipe right to claim a shift.");

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Log.d("Justin", "Dismiss");
                }
            });


            AlertDialog dialog = builder.create();

            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }


    public void getVolunteer() {
        dbRef.child(Constants.DB_NODE_VOLUNTEERS).child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mVolunteer = dataSnapshot.getValue(Volunteer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    //Retrieves full list of shiftIDs
    public void fetchShiftIds(String _city, String _state){
        foundResults = false;

        final String orgQuery = mOrganization.getName();
        final String zipQuery = mOrganization.getZipcode();

        int itemCount = shifts.size();
        shifts.clear();
        mAdapter.notifyItemRangeRemoved(0, itemCount);

        final ArrayList<String> shiftIds = new ArrayList<>();
        DatabaseReference dbShiftsByStateCity = dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_STATECITY);

        Query dbQuery = dbShiftsByStateCity.child(_state).child(_city.toLowerCase()).orderByValue();

        dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;
                long numChildren = dataSnapshot.getChildrenCount();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String searchKey = snapshot.getValue(String.class);
                    String shiftId = snapshot.getKey();

                    count++;
                    if(searchKey.contains(orgQuery) && searchKey.contains(zipQuery)){
                        foundResults = true;
                        fetchShift(shiftId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fetchShift(final String _shiftId){

        dbRef.child(Constants.DB_NODE_SHIFTS).child(_shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Fetching shift: ", _shiftId);
                Shift shift = dataSnapshot.getValue(Shift.class);
                Log.d("Shift value: ", String.valueOf(shift));
                if(!shift.getCurrentVolunteers().contains(userId)){
                    if(isVolunteer) {
                        if(shift.getMinTrust() < mVolunteer.getRating()) {
                            Log.d("Justin", mVolunteer.getRating() + "");
                            Log.d("Justin", shift.getMinTrust() + "");
                            shifts.add(shift);
                        }
                    } else {
                        shifts.add(shift);
                    }


                    mAdapter.notifyDataSetChanged();
                }else{
                    Log.d(TAG, "Volunteer already signed up for shift");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }






    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();

                if(swipeDir == 8){
                    Log.d("shifts size: ", String.valueOf(shifts.size()));
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Volunteer for this shift?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            Log.d("Position ", String.valueOf(position));

                            claimShift(shifts.get(position));
                            mAdapter.notifyItemRemoved(position);
                            shifts.remove(position);
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Log.d("Justin", "Dismiss");
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if(swipeDir == 4){
                    mAdapter.notifyItemRemoved(position);
                    shifts.remove(position);


                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void claimShift(Shift shift){
        final String shiftId = shift.getPushId();

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shift shift = dataSnapshot.getValue(Shift.class);
                if(shift.getMaxVolunteers() - shift.getCurrentVolunteers().size() <= 0){
                    Toast.makeText(mContext, "Shift full", Toast.LENGTH_SHORT).show();
                }else{
                    // Assign to shiftsPending for user
                    dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(userId).child(shiftId).setValue(shiftId);

                    //Add user to list of volunteers and push to database
                    shift.addVolunteer(userId);
                    dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("currentVolunteers").setValue(shift.getCurrentVolunteers());

                    //check if shift has slots left. If not, remove from shiftsAvailable
                    String organizationID = shift.getOrganizationID();
                    String zip = String.valueOf(shift.getZip());
                    String state = shift.getState();
                    String city = shift.getCity();

                    if(shift.getMaxVolunteers() - shift.getCurrentVolunteers().size() == 0){
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_STATECITY).child(state).child(city).child(shiftId).removeValue();
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(organizationID).child(shiftId).removeValue();
                        dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child(zip).child(shiftId).removeValue();
                    }

                    Toast.makeText(mContext, "Shift claimed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
