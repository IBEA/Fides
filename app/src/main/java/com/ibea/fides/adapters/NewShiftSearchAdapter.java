package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Shift;
import com.ibea.fides.ui.activities.ShiftDetailsActivity;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alaina Traxler on 2/7/2017.
 */

public class NewShiftSearchAdapter extends RecyclerView.Adapter<NewShiftSearchAdapter.NewShiftSearchViewHolder> {
    private ArrayList<Shift> mShifts = new ArrayList<>();
    private String mUserId;
    private final String TAG = "NewShiftSearchAdapter";

    public NewShiftSearchAdapter(Context context, ArrayList<Shift> shifts) {
            mShifts = shifts;
            mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }

    @Override
    public NewShiftSearchAdapter.NewShiftSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shift, parent, false);
            NewShiftSearchViewHolder viewHolder = new NewShiftSearchViewHolder(view);
            return viewHolder;
            }

    @Override
    public void onBindViewHolder(NewShiftSearchAdapter.NewShiftSearchViewHolder holder, int position) {
            holder.bindShift(mShifts.get(position));
            }

    @Override
    public int getItemCount() {
            return mShifts.size();
            }

    public class NewShiftSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.textView_OrganizationName) TextView mTextView_OrganizationName;
        @Bind(R.id.textView_ShortDesc) TextView mTextView_ShortDesc;
        @Bind(R.id.textView_StartTime) TextView mTextView_StartTime;
        @Bind(R.id.textView_EndTime) TextView mTextView_EndTime;
        @Bind(R.id.textView_StartDate) TextView mTextView_StartDate;

        private Context mContext;

        public NewShiftSearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindShift(Shift shift) {
            mTextView_OrganizationName.setText(shift.getOrganizationName());
            mTextView_ShortDesc.setText(shift.getShortDescription());
            mTextView_StartTime.setText(shift.getStartTime());
            mTextView_EndTime.setText(shift.getEndTime());
            mTextView_StartDate.setText(shift.getStartDate());
        }

        @Override
        public void onClick(View v){
            //TODO: Parcel shift and send to shift details
            Intent intent = new Intent(mContext, ShiftDetailsActivity.class);
            intent.putExtra("shift", Parcels.wrap(mShifts.get(this.getAdapterPosition())));
            mContext.startActivity(intent);
        }

        public void claimShift(int _position){
            Shift shift = mShifts.get(_position);
            final String shiftId = shift.getPushId();
            Log.d("In claimShift:", shiftId);
            Log.d("Shift desc: ", shift.getShortDescription());

            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

            dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Shift shift = dataSnapshot.getValue(Shift.class);
                    if(shift.getMaxVolunteers() - shift.getCurrentVolunteers().size() <= 0){
                        Toast.makeText(mContext, "Shift full", Toast.LENGTH_SHORT).show();
                    }else{
                        // Assign to shiftsPending for user
                        dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_VOLUNTEERS).child(mUserId).child(shiftId).setValue(shiftId);

                        //Add user to list of volunteers and push to database
                        shift.addVolunteer(mUserId);
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

    }
}