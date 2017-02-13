package com.ibea.fides.adapters;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Shift;
import com.ibea.fides.models.User;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



/**
 * Created by KincaidJ on 1/31/17.
 */

//Deprecated, leave intact to harvest code from

public class FirebaseVolunteerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private View mView;
    private User mUser;
    private Shift mShift;
    private Button mDislikeButton;
    private Button mLikeButton;

    // Popup
    private PopupWindow mPopUp;
    private View mPop;
    private Button mNoShowButton;
    private Button mShowButton;
    private EditText mHoursInput;
    private String mHours;
    private String mDiffInput;

    // Rating System
    final private int DISLIKE = 0;
    final private int LIKE = 3;

    private String shiftId;
    private String mUserId;
    private String indexKey;
    boolean mRated;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public FirebaseVolunteerViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mPop = LayoutInflater.from(itemView.getContext()).inflate(R.layout.popup_rating, (ViewGroup)mView.getParent(), false);
    }

    public void bindUser(String userId, String _shiftId, int position, boolean rated) {
        final TextView userName = (TextView) mView.findViewById(R.id.textView_Name);

        mDislikeButton.setOnClickListener(this);
        mLikeButton.setOnClickListener(this);

        mRated = rated;
        shiftId = _shiftId;
        indexKey = Integer.toString(position);
        mUserId = userId;

        // Retrieve Shift from database
        DatabaseReference shiftRef = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_SHIFTS).child(_shiftId);
        shiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mShift = dataSnapshot.getValue(Shift.class);

                // Retrieve appropriate User from database
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_USERS).child(mUserId);
                ref.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);
                        userName.setText(mUser.getName());

                        // Remove buttons if shift isn't complete OR user has already been rated
                        if(mRated || !mShift.getComplete()) {

                            mLikeButton.setVisibility(View.GONE);
                            mDislikeButton.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onClick(View view) {
        if(view == mDislikeButton) {
            rate(DISLIKE);
        } else if(view == mLikeButton) {
            rate(LIKE);
        } else if(view == mShowButton) {
            mHours = mHoursInput.getText().toString();
            dataUpdate(mHours, true);
        } else if(view == mNoShowButton) {
            dataUpdate(mHours, false);
        }
    }

    private void rate(int rating) {
        // Remove rating buttons from display for user
        mDislikeButton.setVisibility(View.GONE);
        mLikeButton.setVisibility(View.GONE);

        // Retrieve User's rating history and calculate new rating
        List<Integer> ratingHistory = mUser.getRatingHistory();
        ratingHistory.add(rating);
        float size = ratingHistory.size();
        float modifiedRating = 0;
        float modifiedMax = 0;
        float index = 1;
        float modifier = 0;

        for(Integer rate : ratingHistory) {
            modifier = index/size;
            modifiedRating += (modifier * rate);
            modifiedMax += (modifier * LIKE);
            ++index;
        }
        float finalFloatRating = (modifiedRating/modifiedMax) * 100 ;
        int finalRating = Math.round(finalFloatRating);
        mUser.setRating(finalRating);
        mUser.setRatingHistory(ratingHistory);

        // Update Database with new User info and remove User from rated shift
        dbRef.child(Constants.DB_NODE_USERS).child(mUser.getPushId()).setValue(mUser);
        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("currentVolunteers").child(indexKey).removeValue();

        // Retrieve duration of shift information
        // Start and End times/dates
        String startDate = mShift.getStartDate();
        String endDate = mShift.getEndDate();
        String from = mShift.getStartTime();
        String to = mShift.getEndTime();

        // Time and Date format
        SimpleDateFormat tFormat = new SimpleDateFormat("kk:mm");
        SimpleDateFormat dFormat = new SimpleDateFormat("MM-dd-yyyy");

        try {
            double difference = 0.00;

            // If dates aren't equal, calculate difference
            if(!startDate.equals(endDate)) {
                Date date1 = dFormat.parse(startDate);
                Date date2 = dFormat.parse(endDate);
                difference = date2.getTime() - date1.getTime();
            }

            // Calculate difference for the times
            Date time1 = tFormat.parse(from);
            Date time2 = tFormat.parse(to);
            difference += time2.getTime() - time1.getTime();

            // Convert difference to hours and format correctly
            difference = (difference/(60 * 60 * 1000));
            DecimalFormat df = new DecimalFormat("0.00");
            mDiffInput = df.format(difference);

        } catch (ParseException ex){
            ex.printStackTrace();
        }

        // Move User from current volunteers on shift to rated volunteers
        mShift.getCurrentVolunteers().remove(mUser.getPushId());
        mShift.addRated(mUser.getPushId());
        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("currentVolunteers").setValue(mShift.getCurrentVolunteers());
        dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("ratedVolunteers").setValue(mShift.getRatedVolunteers());

        // Call popup
        popup();
    }

    private void popup() {
        mPopUp = new PopupWindow(mPop, 1000, 1000, true);
        mPopUp.showAtLocation(mPop, Gravity.CENTER, 0, 0);
        mShowButton = (Button) mPop.findViewById(R.id.showButton);
        mNoShowButton = (Button) mPop.findViewById(R.id.noShowButton);
        mHoursInput = (EditText) mPop.findViewById(R.id.hoursInput);

        mHoursInput.setText(mDiffInput);
        mShowButton.setOnClickListener(this);
        mNoShowButton.setOnClickListener(this);
    }

    private void dataUpdate(String time, boolean showed) {
        if(showed) {
            double currentHours = mUser.getHours();
            currentHours += Double.parseDouble(time);
            dbRef.child(Constants.DB_NODE_USERS).child(mUser.getPushId()).child("hours").setValue(currentHours);
        } else {
            int absences = mUser.getAbsences();
            absences += 1;
            dbRef.child(Constants.DB_NODE_USERS).child(mUser.getPushId()).child("absences").setValue(absences);
        }
        mPopUp.dismiss();
    }


}
