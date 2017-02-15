package com.ibea.fides.ui.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.OrganizationListAdapter;
import com.ibea.fides.models.Organization;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrganizationSearchFragment extends Fragment implements View.OnClickListener{
    @Bind(R.id.editText_Organization)EditText mEditText_Organization;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.imageButton_Search) ImageButton mImageButton_Search;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private Boolean foundResults = false;
    private Context mContext;
    private ArrayList<Organization> mOrganizations = new ArrayList<>();
    private RecyclerView.Adapter mRecyclerAdapter;

    private final String TAG = "OrgSearchFragment";

    Toast toast;
    View toastView;

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
        mImageButton_Search.setOnClickListener(this);

        mRecyclerAdapter = new OrganizationListAdapter(mContext, mOrganizations);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mEditText_Organization.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // the user is done typing.
                    fetchOrganizationsIds();
                    Log.d("+++++", "the user is done typing");

                    return true; // consume.
                }
                Log.d("+++++", "pass on");
                return false; // pass on to other listeners.
            }
        });

        mEditText_Organization.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d("Action: ", String.valueOf(keyEvent.getAction()));
                Log.d("Keycode: ", String.valueOf(KeyEvent.KEYCODE_ENTER));
                if(keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    Log.d(TAG, "triggered");
                    return false;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == mImageButton_Search){
            fetchOrganizationsIds();
        }
    }

    public void fetchOrganizationsIds(){
        Log.d(TAG, "in fetchShiftIds");

        foundResults = false;

        final String orgQuery = mEditText_Organization.getText().toString().toLowerCase();
        Log.d(TAG, orgQuery);

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
                    Log.d(TAG, orgQuery);

                    if(searchKey.contains(orgQuery)){
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
