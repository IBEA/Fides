package com.ibea.fides.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseShiftViewHolder;
import com.ibea.fides.adapters.OrganizationListAdapter;
import com.ibea.fides.models.Organization;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftSearchFragment extends Fragment {
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Boolean isOrganization;
    private ArrayList<Organization> orgList = new ArrayList<Organization>();

    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference dbShiftsByZip = dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE);
    final DatabaseReference dbOrganizations = dbRef.child(Constants.DB_NODE_ORGANIZATIONS);

    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.searchView_Zipcode) SearchView mSearchView_Zipcode;

    public ShiftSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shifts_available_for_volunteers, container, false);
        ButterKnife.bind(this, view);

        final Context mContext = this.getContext();

        //TODO: Set searchview up to autopopulate with user zipcode
        setUpFirebaseAdapter("97201", "shiftsByZip");
        mRecyclerView.setAdapter(mFirebaseAdapter);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //TODO: implement tag search
        //TODO: implement city search

        //We're passing shiftsByZip in anticipation of further options like tags and cities
        mSearchView_Zipcode.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                String onlyNumbers = "[0-9]+";

                //Make sure the query is not empty
                if(query.length() != 0){
                    //Check to see if it's a zipcode
                    if(query.length() == 5 && query.matches(onlyNumbers)){
                        setUpFirebaseAdapter(query, "shiftsByZip");

                        //Setting vs swapping helps prevent index errors
                        //TODO: get this sorted out so that it's not redundant. We're setting the adapter IN the setup.
                        if(mRecyclerView.getAdapter().getClass() == mFirebaseAdapter.getClass()){
                            mRecyclerView.swapAdapter(mFirebaseAdapter, true);
                        }else{
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

        return view;
    }

    // Store instance variables

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // newInstance constructor for creating fragment with arguments
    public static ShiftSearchFragment newInstance(int page, String title) {
        ShiftSearchFragment fragmentFirst = new ShiftSearchFragment();
        return fragmentFirst;
    }

    private void setUpFirebaseAdapter(String query, String searchType) {
        //Where we should drop the switch in for query type
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, DirtyFirebaseShiftViewHolder>
                (String.class, R.layout.dirty_shift_list_item, DirtyFirebaseShiftViewHolder.class, dbShiftsByZip.child(query)) {
            @Override
            protected void populateViewHolder(DirtyFirebaseShiftViewHolder viewHolder, String shiftId, int position) {
                viewHolder.bindShift(shiftId, false);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}
