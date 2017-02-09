package com.ibea.fides.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.NewShiftSearchAdapter;
import com.ibea.fides.models.Shift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewShiftSearchFragment extends Fragment implements View.OnClickListener{
    @Bind(R.id.searchView_City) SearchView mSearchView_City;
    @Bind(R.id.searchView_State) SearchView mSearchView_State;
    @Bind(R.id.searchView_Zip) SearchView mSearchView_Zip;
    @Bind(R.id.searchView_Organization) SearchView mSearchView_Organization;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.button_Search) Button mButton_Search;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private final String TAG = "NewShiftsSearchFragment";
    private ArrayList<Shift> shifts = new ArrayList<>();
    private RecyclerView.Adapter mRecyclerAdapter;
    private Context mContext;

    private String userId;

    public NewShiftSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_shift_search, container, false);
        ButterKnife.bind(this, view);
        mContext = this.getContext();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //TODO: Replace with population from users once all users are required to have these fields. Don't forget you're doing this in onResume as well!

        mSearchView_State.setQuery("OR", false);
        mSearchView_City.setQuery("Portland", false);

        mRecyclerAdapter = new NewShiftSearchAdapter(mContext, shifts);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        setRecyclerViewItemTouchListener();

        mButton_Search.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        //TODO: Allow searches when the zipcode, but not the city, is in.
        //TODO: Consider abandoning zipcode searches

        if(view == mButton_Search){
            //TODO: Lowercase cityQuery once database also has lowercase city nodes
            String cityQuery = mSearchView_City.getQuery().toString();
            String stateQuery = mSearchView_State.getQuery().toString();
            String zipQuery = mSearchView_Zip.getQuery().toString();
            String orgQuery = mSearchView_Organization.getQuery().toString();

            //TODO: Remove stateQuery check once state dropdown is in
            if(cityQuery.length() != 0 && stateQuery.length() != 0 && validateZip(zipQuery)){
                Boolean filterByZip;
                Boolean filterByOrg;

                filterByZip = zipQuery.length() != 0;
                filterByOrg = orgQuery.length() != 0;

                //Sets off a series of functions that fetches shift Ids, resolves them, and then filters them.
                fetchShiftIds(cityQuery, stateQuery, filterByZip, filterByOrg);
            }else{
                if(cityQuery.length() == 0){
                    Toast.makeText(mContext, "Please enter a city", Toast.LENGTH_SHORT).show();
                }else if(stateQuery.length() == 0){
                    Toast.makeText(mContext, "Please enter a valid state", Toast.LENGTH_SHORT).show();
                }else if(!validateZip(zipQuery)){
                    Toast.makeText(mContext, "Invalid zip code", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Retrieves full list of shiftIDs
    public void fetchShiftIds(String _city, String _state, final Boolean filterByZip, final Boolean filterByOrg){
        Log.d(TAG, "in fetchShiftIds");

        final String zipQuery = mSearchView_Zip.getQuery().toString();
        final String orgQuery = mSearchView_Organization.getQuery().toString().toLowerCase();

        if(validateZip(zipQuery)){
            String w = "(.*)";
            final String query = w + zipQuery + w + orgQuery + w;
            Log.d(TAG, "Query: " + query);

            int itemCount = shifts.size();
            shifts.clear();
            mRecyclerAdapter.notifyItemRangeRemoved(0, itemCount);

            final ArrayList<String> shiftIds = new ArrayList<>();
            DatabaseReference dbShiftsByStateCity = dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_STATECITY);

            Query dbQuery = dbShiftsByStateCity.child(_state).child(_city).orderByValue();
            Log.d(TAG, "State: " + _state);
            Log.d(TAG, "City: " + _city);
            dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.d(TAG, "fetching shifts");
                    Log.d(TAG, String.valueOf(dataSnapshot.getChildrenCount()));
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        String searchKey = snapshot.getValue(String.class);
                        String shiftId = snapshot.getKey();

                        Log.d(TAG, searchKey);

                        if(searchKey.matches(query)){
                            Log.d(TAG, "Query matches!");
                            fetchShift(shiftId, filterByZip, filterByOrg);
                        }

                    }
                    Collections.sort(shifts, new Comparator<Shift>() {
                        @Override
                        public int compare(Shift shift2, Shift shift1)
                        {
                            return  shift1.getStartDate().compareTo(shift2.getStartDate());
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void fetchShift(String _shiftId, final Boolean filterByZip, final Boolean filterByOrg){
        Log.d(TAG, "in fetchShift");
        final String zipQuery = mSearchView_Zip.getQuery().toString();
        final String orgQuery = mSearchView_Organization.getQuery().toString();

        dbRef.child(Constants.DB_NODE_SHIFTS).child(_shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shift shift = dataSnapshot.getValue(Shift.class);

                if(!shift.getCurrentVolunteers().contains(userId)){
                    shifts.add(shift);
                    mRecyclerAdapter.notifyItemInserted(shifts.indexOf(shift));
                }else{
                    Log.d(TAG, "User already signed up for shift");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Boolean validateZip(String _query){
        String onlyNumbers = "[0-9]+";

        if(_query.length() != 0){
            if(_query.length() == 5 && _query.matches(onlyNumbers)){
                return true;
            }else{
                Toast.makeText(mContext, "Invalid zip code", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSearchView_Zip.setQuery("", false);
        mSearchView_City.setQuery("", false);
        mSearchView_State.setQuery("", false);
        mSearchView_Organization.setQuery("", false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSearchView_State.setQuery("OR", false);
        mSearchView_City.setQuery("Portland", false);
    }

    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
//                Log.d("Adapter Position: ", String.valueOf(position));

                if(swipeDir == 8){
                    ((NewShiftSearchAdapter.NewShiftSearchViewHolder) viewHolder).claimShift(position);
                }
                shifts.remove(position);
                mRecyclerView.getAdapter().notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
