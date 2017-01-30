package com.ibea.fides.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseShiftViewHolder;
import com.ibea.fides.models.Shift;
import com.ibea.fides.utils.RecyclerItemListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftsTestingActivity extends BaseActivity implements RecyclerItemListener{
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerItemListener mTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts_testing);
        ButterKnife.bind(this);

        mTransfer = this;

        setUpFirebaseAdapter();
    }

    @Override
    public void userItemClick(Object data, String view){
        Shift shift = (Shift) data;
        if(view.equals("Volunteer")){
//            claimShift(shift);
        }else if(view.equals("Cancel")){

        }else{
            //!! Redirect to shift details!!
        }
    }

    private void setUpFirebaseAdapter() {

        //!! If statement to switch between zip and organization needed here. setUpFirebaseAdapter will need to take appropriate arguments. Defaulting to a zip code 97201 right now !!
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, DirtyFirebaseShiftViewHolder>
                (String.class, R.layout.dirty_shift_list_item, DirtyFirebaseShiftViewHolder.class, dbShiftsAvailable.child(Constants.DB_SUBNODE_ZIPCODE).child("97201")) {

            @Override
            protected void populateViewHolder(DirtyFirebaseShiftViewHolder viewHolder, String shiftID, int position) {
                Log.v(TAG, "In populateViewHolder");
                viewHolder.bindShift(shiftID, false );
            }
        };

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }
}
