package com.ibea.fides.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseShiftViewHolder;
import com.ibea.fides.models.Shift;
import com.ibea.fides.utils.RecyclerItemListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftsTestingActivity extends BaseActivity implements View.OnClickListener, RecyclerItemListener{
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
//    @Bind(R.id.button_Volunteer) Button mButton_Volunteer;

    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerItemListener mTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts_testing);
        ButterKnife.bind(this);

        mTransfer = this;

        setUpFirebaseAdapter();
//        mButton_Volunteer.setOnClickListener(this);
    }

    @Override
    public void userItemClick(Object data){
        Shift shift = (Shift) data;
        Toast.makeText(mContext, shift.getOrganizationName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v){
        //!! Put protections in for shifts that have been claimed before the interface updates !!
//        if(v == mButton_Volunteer){
//            claimShift();
//        }
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, DirtyFirebaseShiftViewHolder>
                (String.class, R.layout.dirty_shift_list_item, DirtyFirebaseShiftViewHolder.class, dbShiftsAvailable.child(Constants.DB_SUBNODE_ZIPCODE).child("97201")) {

            @Override
            protected void populateViewHolder(DirtyFirebaseShiftViewHolder viewHolder, String shiftID, int position) {
                viewHolder.bindShift(shiftID, mTransfer);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    public void claimShift(){
//        String shiftID =

        // Assign to shiftsPending for user
//        dbShiftsPending.child(Constants.DB_SUBNODE_VOLUNTEERS).child(mCurrentUser.getUid()).child(shif)

        //check if shift has slots left. If not, remove from shiftsAvailable
        Toast.makeText(mContext, "Shift claimed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }
}
