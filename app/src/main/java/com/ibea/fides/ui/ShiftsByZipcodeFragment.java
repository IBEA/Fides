package com.ibea.fides.ui;


import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseShiftViewHolder;
import com.ibea.fides.adapters.OrganizationListAdapter;
import com.ibea.fides.adapters.OrganizationListAdapter;
import com.ibea.fides.models.Organization;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsByZipcodeFragment extends Fragment {
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Organization> orgList = new ArrayList<Organization>();

    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.searchView_Zipcode) SearchView mSearchView_Zipcode;

    public ShiftsByZipcodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shifts_by_zipcode, container, false);
        ButterKnife.bind(this, view);

        final Context mContext = this.getContext();

        Log.v(">>>>", "In onCreateView");

        //!! Set searchview up to autopopulate with user zipcode !!
//        setUpFirebaseAdapter("97201", "shiftsByZip");

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference dbShiftsByZip = dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE);
        final DatabaseReference dbOrganizations = dbRef.child(Constants.DB_NODE_ORGANIZATIONS);

//        mRecyclerView.setAdapter(mFirebaseAdapter);

        mSearchView_Zipcode.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                String onlyNumbers = "[0-9]+";

                if(query.length() != 0){
                    if(query.length() == 5 && query.matches(onlyNumbers)){
                        Log.v("-----", "onlyNumbers");

//                        mFirebaseAdapter = null;
                        setUpFirebaseAdapter(query, "shiftsByZip");
//                        mRecyclerView.setAdapter(mFirebaseAdapter);
                        if(mRecyclerView.getAdapter().getClass() == mFirebaseAdapter.getClass()){
                            mRecyclerView.swapAdapter(mFirebaseAdapter, true);
                        }else{
                            mRecyclerView.setAdapter(mFirebaseAdapter);
                        }

                    }else{
                        dbOrganizations.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.v("-----", "In org call");
                                orgList.clear();
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    Log.v("-----", "In org loop");
                                    Organization organization = snapshot.getValue(Organization.class);
                                    if(organization.getName().toLowerCase().contains(query.toLowerCase())){
                                        orgList.add(organization);
                                        Log.v("-----", organization.getName() + " added");
                                    }

                                }
                                mRecyclerAdapter = new OrganizationListAdapter(mContext, orgList);
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

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)

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

    private void setUpFirebaseAdapter(String query, String searchType) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_ZIPCODE).child(query);

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
