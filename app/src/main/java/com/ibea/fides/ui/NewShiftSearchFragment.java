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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewShiftSearchFragment extends Fragment {
    @Bind(R.id.searchView_City) SearchView mSearchView_City;
    @Bind(R.id.searchView_State) SearchView mSearchView_State;
    @Bind(R.id.searchView_Zip) SearchView mSearchView_Zip;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private final String TAG = "NewShiftsSearchFragment";
    private ArrayList<Shift> shifts = new ArrayList<>();
    private RecyclerView.Adapter mRecyclerAdapter;
    private Context mContext;

    public NewShiftSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_shift_search, container, false);
        ButterKnife.bind(this, view);
        mContext = this.getContext();



        mRecyclerAdapter = new NewShiftSearchAdapter(mContext, shifts);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));


        mSearchView_City.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView_City.clearFocus();

                //Sets off a series of functions that fetches shift Ids, resolves them, and then filters them.
                fetchShiftIds(query, "OR");
                //TODO: Send filteredShifts to a RecyclerAdapter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    //Retrieves full list of shiftIDs
    public void fetchShiftIds(String _city, String _state){
        Log.d(TAG, "in fetchShifts");

        shifts.clear();

        final ArrayList<String> shiftIds = new ArrayList<>();
        DatabaseReference dbShiftsByStateCity = dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_STATECITY);

        Query query = dbShiftsByStateCity.child(_state).child(_city).orderByKey().limitToFirst(100);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "fetching shifts");
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String shiftId = snapshot.getValue(String.class);
                    shiftIds.add(shiftId);
                }
                resolveShiftsandFilter(shiftIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "Outside for loop, starting filters");

        String zipQuery = mSearchView_Zip.getQuery().toString();

//                if(validateZip(query)){
//                    Log.d(TAG, "Valid Zip, filtering");
//                    filterByZip(query);
//                }
        Log.d(TAG, "Size: " + shifts.size());
        mRecyclerAdapter.notifyDataSetChanged();
    }

//    public void fetchShift(String _shiftId){
//        //Array type is necessary for use in dbRef call
//
//        dbRef.child(Constants.DB_NODE_SHIFTS).child(_shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Shift shift = dataSnapshot.getValue(Shift.class);
//                Log.d(TAG, shift.getOrganizationName());
//                shifts.add(shift);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void resolveShiftsandFilter(ArrayList<String> _shiftIds){
        fetchShifts(_shiftIds, 0);
    }

    public void fetchShifts(final ArrayList<String> _shiftIds, final int _position){
        Log.d(TAG, String.valueOf(_position));
        dbRef.child(Constants.DB_NODE_SHIFTS).child(_shiftIds.get(_position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Shift shift = dataSnapshot.getValue(Shift.class);
                shifts.add(shift);
                if(_position != _shiftIds.size() - 1){
                    fetchShifts(_shiftIds, _position + 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<Shift> filterByZip(String _query){
        for(int i = 0; i < shifts.size(); i++){
            if(!shifts.get(i).getZip().equals(_query)){
                shifts.remove(i);
            }
        }
        return shifts;
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
        }else return false;

    }
}
