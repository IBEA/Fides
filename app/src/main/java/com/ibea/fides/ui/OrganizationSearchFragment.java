package com.ibea.fides.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.NewShiftSearchAdapter;
import com.ibea.fides.adapters.OrganizationListAdapter;
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.Shift;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrganizationSearchFragment extends Fragment implements View.OnClickListener{
    @Bind(R.id.searchView_Organization)SearchView mSearchView_Organization;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.button_Search) Button mButton_Search;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private Boolean foundResults = false;
    private Context mContext;
    private ArrayList<Organization> mOrganizations = new ArrayList<>();
    private RecyclerView.Adapter mRecyclerAdapter;

    private final String TAG = "OrgSearchFragment";

    public OrganizationSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organization_search, container, false);
        ButterKnife.bind(this, view);

        mContext = this.getContext();
        mButton_Search.setOnClickListener(this);

        mRecyclerAdapter = new OrganizationListAdapter(mContext, mOrganizations);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == mButton_Search){
            fetchOrganizationsIds();
        }
    }

    public void fetchOrganizationsIds(){
        Log.d(TAG, "in fetchShiftIds");

        foundResults = false;

        final String orgQuery = mSearchView_Organization.getQuery().toString().toLowerCase();

        String w = "(.*)";
        final String query = w + orgQuery + w;

        int itemCount = mOrganizations.size();
        mOrganizations.clear();
        mRecyclerAdapter.notifyItemRangeRemoved(0, itemCount);

        final ArrayList<String> shiftIds = new ArrayList<>();
        DatabaseReference dbSearchOrganizations = dbRef.child(Constants.DB_NODE_SEARCH).child(Constants.DB_SUBNODE_ORGANIZATIONS);

        Query dbQuery = dbSearchOrganizations.orderByValue();
        dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "fetching organizations");

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String searchKey = snapshot.getValue(String.class);
                    String organizationId = snapshot.getKey();

                    Log.d(TAG, searchKey);

                    if(searchKey.matches(query)){
                        Log.d(TAG, "Query matches!");
                        foundResults = true;
                        fetchOrganization(organizationId);
                    }
                }

                if(!foundResults){
                    Toast.makeText(mContext, "No organizations found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fetchOrganization(String _organizationId){
        Log.d(TAG, "in fetchShift");

        dbRef.child(Constants.DB_NODE_ORGANIZATIONS).child(_organizationId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Organization organization = dataSnapshot.getValue(Organization.class);

                mOrganizations.add(organization);
                mRecyclerAdapter.notifyItemInserted(mOrganizations.indexOf(organization));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
