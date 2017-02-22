package com.ibea.fides.ui.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.ibea.fides.models.Volunteer;
import com.ibea.fides.ui.activities.SearchActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftSearchFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    @Bind(R.id.editText_City) EditText mEditText_City;
    @Bind(R.id.stateSpinner) Spinner mStateSpinner;
    @Bind(R.id.editText_Zip) EditText mEditText_Zip;
    @Bind(R.id.editText_Organization) EditText mEditText_Organization;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.imageButton_Search) ImageButton mImageButton_Search;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public SearchActivity parentActivity;

    private String mState;
    private final String TAG = "NewShiftsSearchFragment";
    private ArrayList<Shift> shifts = new ArrayList<>();
    private RecyclerView.Adapter mRecyclerAdapter;
    private Context mContext;
    private Boolean foundResults = false;

    private Volunteer mVolunteer;
    private ShiftSearchFragment mThis;

    private String userId;

    public ShiftSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_shift_search, container, false);
        ButterKnife.bind(this, view);
        mContext = this.getContext();

        mThis = this;
        this.setHasOptionsMenu(true);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.states_array,R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_list);
        mStateSpinner.setAdapter(adapter);

        autoFill();

        mRecyclerAdapter = new NewShiftSearchAdapter(mContext, shifts);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        setRecyclerViewItemTouchListener();

        mEditText_Organization.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // the user is done typing.
                    startSearch();
                    return true; // consume.
                }
                return false; // pass on to other listeners.
            }
        });

        mEditText_City.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // the user is done typing.
                    startSearch();
                    return true; // consume.
                }
                return false; // pass on to other listeners.
            }
        });

        mEditText_Zip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // the user is done typing.
                    startSearch();
                    return true; // consume.
                }
                return false; // pass on to other listeners.
            }
        });

        mImageButton_Search.setOnClickListener(this);

        mStateSpinner.setOnItemSelectedListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    public void autoFill() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef = dbRef.child(Constants.DB_NODE_VOLUNTEERS).child(userId);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mVolunteer = dataSnapshot.getValue(Volunteer.class);

                mEditText_City.setText(mVolunteer.getCity());

                Resources res = getResources();
                String[] states = res.getStringArray(R.array.states_array);
                int index = Arrays.asList(states).indexOf(mVolunteer.getState());

                mStateSpinner.setSelection(index);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View view) {

        if(view == mImageButton_Search){
            mImageButton_Search.setOnClickListener(null);
            startSearch();
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mState = parent.getItemAtPosition(pos).toString();


    }

    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void startSearch(){
        String cityQuery = mEditText_City.getText().toString().toLowerCase();
        String zipQuery = mEditText_Zip.getText().toString();
        String orgQuery = mEditText_Organization.getText().toString();

        if(cityQuery.length() != 0 && validateZip(zipQuery)){
            //Sets off a series of functions that fetches shift Ids, resolves them, and then filters them.
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            fetchShiftIds(cityQuery, mState);
        }else{
            if(cityQuery.length() == 0){
                Toast.makeText(mContext, "Please enter a city", Toast.LENGTH_SHORT).show();
            }else if(!validateZip(zipQuery)){
                Toast.makeText(mContext, "Invalid zip code", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Retrieves full list of shiftIDs
    public void fetchShiftIds(String _city, String _state){
        foundResults = false;

        final String zipQuery = mEditText_Zip.getText().toString();
        final String orgQuery = mEditText_Organization.getText().toString().toLowerCase();

        if(validateZip(zipQuery)){
            int itemCount = shifts.size();
            shifts.clear();
            mRecyclerAdapter.notifyItemRangeRemoved(0, itemCount);

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
                            fetchShift(shiftId, count == numChildren);
                        }

                    }

                    if(!foundResults){
                        Toast.makeText(mContext, "No opportunities found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void fetchShift(final String _shiftId, final Boolean _lastItem){

        dbRef.child(Constants.DB_NODE_SHIFTS).child(_shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Fetching shift: ", _shiftId);
                Shift shift = dataSnapshot.getValue(Shift.class);
                Log.d("Shift value: ", String.valueOf(shift));
                if(!shift.getCurrentVolunteers().contains(userId)){
                    if(shift.getMinTrust() <= mVolunteer.getRating()) {
                        shifts.add(shift);
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                }else{
                    Log.d(TAG, "Volunteer already signed up for shift");
                }

                if(_lastItem){
                    mImageButton_Search.setOnClickListener(mThis);
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
    }

    @Override
    public void onResume() {
        super.onResume();
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
                           mRecyclerAdapter.notifyItemRemoved(position);
                           shifts.remove(position);
                       }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            mRecyclerAdapter.notifyDataSetChanged();
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
                    mRecyclerAdapter.notifyItemRemoved(position);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_volunteertutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//          TODO: Set up an actual message
            builder.setMessage("This is the Shift Search Page. You can search for volunteer opportunities here. You must supply the city and state. Swipe right on a shift to volunteer for it.");

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
}
