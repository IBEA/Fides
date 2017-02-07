package com.ibea.fides.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Shift;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewShiftSearchFragment extends Fragment {
    @Bind(R.id.searchView_City) SearchView mSearchView_City;
    @Bind(R.id.searchView_State) SearchView mSearchView_State;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public NewShiftSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSearchView_City.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView_City.clearFocus();
                ArrayList<String> shiftIds = fetchShifts();
                ArrayList<Shift> filteredShifts = filterShifts(shiftIds);
                //TODO: Send filteredShifts to a RecyclerAdapter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_shift_search, container, false);
    }

    //Retrieves full list of shiftIDs
    public ArrayList<String> fetchShifts(){
        ArrayList<String> shiftIds = new ArrayList<>();

        Query query = dbRef.child(Constants.DB_NODE_SHIFTS).orderByKey().limitToFirst(100);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String catcher = snapshot.getValue(String.class);
                    Log.d(">>>>>", catcher);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return shiftIds;
    }

    public ArrayList<Shift> filterShifts(ArrayList<String> _shiftIds){
        ArrayList<Shift> filteredShifts = new ArrayList<>();
        return filteredShifts;
    }

}
