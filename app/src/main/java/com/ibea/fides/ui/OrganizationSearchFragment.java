package com.ibea.fides.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrganizationSearchFragment extends Fragment implements View.OnClickListener{
    @Bind(R.id.searchView_Organization)SearchView mSearchView_Organization;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    private Boolean foundResults = false;

    private final String TAG = "OrganizationSearchFragment";

    public OrganizationSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organization_search, container, false);
        ButterKnife.bind(this, view);

        mSearchView_Organization.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == mSearchView_Organization){
            fetchOrganizationsIds();
        }
    }

    public void fetchOrganizationsIds(){
//        Log.d(TAG, "in fetchShiftIds");
//
//        foundResults = false;
//
//        final String zipQuery = mSearchView_Zip.getQuery().toString();
//        final String orgQuery = mSearchView_Organization.getQuery().toString().toLowerCase();
//
//        if(validateZip(zipQuery)){
//            String w = "(.*)";
//            final String query = w + zipQuery + w + orgQuery + w;
//            Log.d(TAG, "Query: " + query);
//
//            int itemCount = shifts.size();
//            shifts.clear();
//            mRecyclerAdapter.notifyItemRangeRemoved(0, itemCount);
//
//            final ArrayList<String> shiftIds = new ArrayList<>();
//            DatabaseReference dbShiftsByStateCity = dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_STATECITY);
//
//            Query dbQuery = dbShiftsByStateCity.child(_state).child(_city).orderByValue();
//            Log.d(TAG, "State: " + _state);
//            Log.d(TAG, "City: " + _city);
//            dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    Log.d(TAG, "fetching shifts");
//
//                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                        String searchKey = snapshot.getValue(String.class);
//                        String shiftId = snapshot.getKey();
//
//                        Log.d(TAG, searchKey);
//
//                        if(searchKey.matches(query)){
//                            Log.d(TAG, "Query matches!");
//                            foundResults = true;
//                            fetchShift(shiftId);
//                        }
//
//                    }
//
//                    if(!foundResults){
//                        Toast.makeText(mContext, "No opportunities found", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
    }

    public void fetchOrganization(){

    }
}
