package com.ibea.fides.ui;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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
import com.ibea.fides.adapters.OrganizationListAdapter;
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.Shift;
import com.ibea.fides.utils.AdapterUpdateInterface;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftSearchFragment extends Fragment implements AdapterUpdateInterface{
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView.Adapter mRecyclerAdapter;

    private Boolean lock = true;
    private String currentUserId;
    private Boolean isOrganization;
    private ArrayList<Organization> orgList = new ArrayList<Organization>();

    private View mView;
    private ShiftSearchFragment mThis;

    private String currentQuery;

    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference dbShiftsByZip = dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE);
    final DatabaseReference dbOrganizations = dbRef.child(Constants.DB_NODE_ORGANIZATIONS);

    @Bind(R.id.unratedRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.searchView_Zipcode) SearchView mSearchView_Zipcode;

    public ShiftSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_volunteer_shifts_search, container, false);
        ButterKnife.bind(this, view);

        mThis = this;
        mView = view;
        final Context mContext = this.getContext();

        isOrganization = PreferenceManager.getDefaultSharedPreferences(this.getContext()).getBoolean(Constants.KEY_ISORGANIZATION, false);
        currentQuery = "97201";
        //TODO: Set searchview up to autopopulate with user zipcode
        setUpFirebaseAdapter(currentQuery, "shiftsByZip");

        mRecyclerView.setAdapter(mFirebaseAdapter);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(currentUserId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(!lock){
                        if(mRecyclerView.getAdapter().getClass() == mFirebaseAdapter.getClass()){
                            mFirebaseAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d("ChildRemoved", "Triggered");

                    //TODO: Move query functionality into separate function, store current query type in member variable and update appropriately
                    if(mRecyclerView.getAdapter().getClass() == mFirebaseAdapter.getClass()){
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

            //TODO: implement tag search
            //TODO: implement city search
            //TODO: implement state search

            //We're passing shiftsByZip in anticipation of further options like tags and cities
            mSearchView_Zipcode.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String query) {
                    String onlyNumbers = "[0-9]+";
                    currentQuery = query;

                    //If you remove this, the query double submits. I have no idea why.
                    mSearchView_Zipcode.clearFocus();

                    //Make sure the query is not empty
                    if(query.length() != 0){
                        //Check to see if it's a zipcode
                        if(query.length() == 5 && query.matches(onlyNumbers)){
                            setUpFirebaseAdapter(query, "shiftsByZip");

                            //Setting vs swapping helps prevent index errors
                            //TODO: get this sorted out so that it's not redundant. We're setting the adapter IN the setup.
                            if(mRecyclerView.getAdapter().getClass() == mFirebaseAdapter.getClass()){
                                Log.d(">>>>>", "Adapter is same");
                                mRecyclerView = null;
                                mRecyclerView = (RecyclerView) mView.findViewById(R.id.unratedRecyclerView);
                                mRecyclerView.setAdapter(mFirebaseAdapter);
                            }else{
                                Log.d(">>>>>", "Adapter is different");
                                mRecyclerView.setAdapter(mFirebaseAdapter);
                            }

                        }else{
                            //Go get a list of orgs, and filter them by the query
                            dbOrganizations.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    orgList.clear();
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        Organization organization = snapshot.getValue(Organization.class);
                                        if(organization.getName().toLowerCase().contains(query.toLowerCase())){
                                            orgList.add(organization);
                                        }
                                    }
                                    mRecyclerAdapter = new OrganizationListAdapter(mContext, orgList);
                                    //Setting vs swapping helps prevent index errors
                                    if(mRecyclerView.getAdapter().getClass() == mRecyclerAdapter.getClass()){
                                        mRecyclerView.swapAdapter(mRecyclerAdapter, true);
                                    }else{
                                        mRecyclerView.setAdapter(mRecyclerAdapter);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return view;
    }

    // Store instance variables

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void updateAdapter(){
        mFirebaseAdapter.notifyDataSetChanged();
    }

    // newInstance constructor for creating fragment with arguments
    public static ShiftSearchFragment newInstance(int page, String title) {
        return new ShiftSearchFragment();
    }

    private void setUpFirebaseAdapter(String query, String searchType) {
        //Where we should drop the switch in for query type

        DatabaseReference dbNode;
        if(searchType.equals("shiftsByZip")){
            dbNode = dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child(query);
        }else{
            dbNode = dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child(query);
        }

        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, FirebaseShiftViewHolder>
                (String.class, R.layout.list_item_shift_pending, FirebaseShiftViewHolder.class, dbNode) {

            @Override
            protected void populateViewHolder(final FirebaseShiftViewHolder viewHolder, final String shiftId, int position) {
                dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Shift shift = dataSnapshot.getValue(Shift.class);

                        viewHolder.bindShift(shift, isOrganization, "ShiftsSearch", mThis);
                        Log.d("On " + shift.getShortDescription() + "?", String.valueOf(shift.getCurrentVolunteers().contains(currentUserId)));

                        if(shift.getCurrentVolunteers().contains(currentUserId)){
                            viewHolder.hideView();
                        }else{
                            viewHolder.showView();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}
