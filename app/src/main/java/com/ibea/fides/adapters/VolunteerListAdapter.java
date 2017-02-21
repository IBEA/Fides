package com.ibea.fides.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Shift;
import com.ibea.fides.models.Volunteer;
import com.ibea.fides.ui.activities.VolunteerProfileActivity;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VolunteerListAdapter extends RecyclerView.Adapter<VolunteerListAdapter.VolunteerViewHolder> {
    private ArrayList<Volunteer> mVolunteers = new ArrayList<>();
    private Volunteer mVolunteer;
    private List<String> mUnratedVolunteers = new ArrayList<>();
    private Shift mShift;
    private int mRating;

    // Popup
    private PopupWindow mPopUp;
    private View mPopupContext;
//    private TextView mNoShowButton;
    private Button mShowButton;
    private EditText mHoursInput;
    private String mHours;
    private String mDiffInput;

    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    StorageReference mImageRef;

    // Rating System
    final private int DISLIKE = 0;
    final private int LIKE = 3;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public VolunteerListAdapter(Context context, ArrayList<Volunteer> volunteers, List<String> _unratedVolunteers, Shift _shift) {
        mVolunteers = volunteers;
        mUnratedVolunteers = _unratedVolunteers;
        mShift = _shift;

    }

    @Override
    public VolunteerListAdapter.VolunteerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_volunteer, parent, false);
        VolunteerViewHolder viewHolder = new VolunteerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VolunteerListAdapter.VolunteerViewHolder holder, int position) {
        holder.bindVolunteer(mVolunteers.get(position));
    }

    @Override
    public int getItemCount() {
        return mVolunteers.size();
    }

    public class VolunteerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Context mContext;
        @Bind(R.id.textView_Name) TextView mTextView_Name;
        @Bind(R.id.imageView_VolunteerThumbnail) ImageView volunteerThumbnail;
        @Bind(R.id.textView_Trust) TextView mTextView_Trust;

        public VolunteerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            mPopupContext = LayoutInflater.from(itemView.getContext()).inflate(R.layout.popup_rating, (ViewGroup)itemView.getParent(), false);
            itemView.setOnClickListener(this);
        }

        public void bindVolunteer(Volunteer volunteer) {
            mVolunteer = volunteer;

            String volId = mVolunteer.getUserId();
            // assign image storage reference variables
            mStorage = FirebaseStorage.getInstance();
            mStorageRef = mStorage.getReferenceFromUrl("gs://fides-6faeb.appspot.com");
            mImageRef = mStorageRef.child("images/" + volId + ".jpg");

            mImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(mContext)
                            .load(uri)
                            .placeholder(R.drawable.avatar_blank)
                            .into(volunteerThumbnail);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            Log.d("Binding " + mVolunteer.getName(), "@");

            Log.d(">>>>>", String.valueOf(mUnratedVolunteers));
            mTextView_Name.setText(mVolunteer.getName());

            if(mVolunteer.getRatingHistory().size() == 0) {
                mTextView_Trust.setText("100%");
            } else {
                mTextView_Trust.setText( Integer.toString (mVolunteer.getRating()) + "%" );
            }

            if(mUnratedVolunteers.contains(mVolunteer.getUserId()) && mShift.getComplete()){
                Log.d(">>>>>", mVolunteer.getName() + " is unrated");
                mTextView_Name.setTextColor(Color.parseColor("#F44336"));
            }
        }

        @Override
        public void onClick(View view){
            if(view == mShowButton) {
                mHours = mHoursInput.getText().toString();
                if(Float.parseFloat(mHours) > 0.1f) {
                    dataUpdate(mHours, true);
                } else if((Float.parseFloat(mHours) == 0.0f) && (mRating > 0)) {
                    Toast.makeText(mContext, "Please enter a positive number of hours", Toast.LENGTH_LONG).show();
                }else {
                    dataUpdate(mHours, false);
                }
            }
            else {
                Intent intent = new Intent(mContext, VolunteerProfileActivity.class);
                intent.putExtra("volunteer", Parcels.wrap(mVolunteers.get(getAdapterPosition())));
                mContext.startActivity(intent);
            }
        }

        public String getVolunteerId(){
            return mVolunteers.get(getAdapterPosition()).getUserId();
        }

        public void popup(int rating) {
            mRating = rating;
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

            mPopUp = new PopupWindow(mPopupContext, 1000, 1000, true);
            mPopUp.showAtLocation(mPopupContext, Gravity.CENTER, 0, 0);
            mShowButton = (Button) mPopupContext.findViewById(R.id.showButton);
//            mNoShowButton = (TextView) mPopupContext.findViewById(R.id.noShowButton);
            mHoursInput = (EditText) mPopupContext.findViewById(R.id.hoursInput);

            mHoursInput.setText(mDiffInput);
            mShowButton.setOnClickListener(this);
//            mNoShowButton.setOnClickListener(this);
        }

        public Boolean isUnrated(){
            Volunteer volunteer = mVolunteers.get(getAdapterPosition());
            return mUnratedVolunteers.contains(volunteer.getUserId());
        }

        private void dataUpdate(final String time, final boolean showed) {
            final Volunteer volunteer = mVolunteers.get(getAdapterPosition());
            dbRef.child(Constants.DB_NODE_SHIFTS).child(mShift.getPushId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mShift = dataSnapshot.getValue(Shift.class);
                    if(!mShift.getRatedVolunteers().contains(volunteer.getUserId())){
                        if(showed) {
                            setRating();
                            double currentHours = volunteer.getHours();
                            currentHours += Double.parseDouble(time);
                            dbRef.child(Constants.DB_NODE_VOLUNTEERS).child(volunteer.getUserId()).child("hours").setValue(currentHours);
                        } else {
                            setRating();
                            int absences = volunteer.getAbsences();
                            absences += 1;
                            dbRef.child(Constants.DB_NODE_VOLUNTEERS).child(volunteer.getUserId()).child("absences").setValue(absences);
                        }
                        mTextView_Name.setTextColor(Color.parseColor("#757575"));
                        mPopUp.dismiss();
                    }else{
                        mUnratedVolunteers.remove(mUnratedVolunteers.indexOf(volunteer.getUserId()));
                        Toast.makeText(mContext, "Volunteer already rated", Toast.LENGTH_SHORT).show();
                        mPopUp.dismiss();
                        mTextView_Name.setTextColor(Color.parseColor("#757575"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setRating(){
            Volunteer volunteer = mVolunteers.get(getAdapterPosition());

            // Retrieve Volunteer's rating history and calculate new rating
            List<Integer> ratingHistory = volunteer.getRatingHistory();
            ratingHistory.add(mRating);
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
            volunteer.setRating(finalRating);
            volunteer.setRatingHistory(ratingHistory);

            // Update Database with new Volunteer info and remove Volunteer from rated shift
            dbRef.child(Constants.DB_NODE_VOLUNTEERS).child(volunteer.getUserId()).setValue(volunteer);
            dbRef.child(Constants.DB_NODE_SHIFTS).child(mShift.getPushId()).child("currentVolunteers").child(volunteer.getUserId()).removeValue();

            // Move Volunteer from current volunteers on shift to rated volunteers
            mShift.getCurrentVolunteers().remove(volunteer.getUserId());
            mShift.addRated(volunteer.getUserId());

            dbRef.child(Constants.DB_NODE_SHIFTS).child(mShift.getPushId()).child("currentVolunteers").setValue(mShift.getCurrentVolunteers());
            dbRef.child(Constants.DB_NODE_SHIFTS).child(mShift.getPushId()).child("ratedVolunteers").setValue(mShift.getRatedVolunteers());

            //Remove user from local unrated list
            Log.d(">>>>>", String.valueOf(mUnratedVolunteers));
            Log.d(">>>>>", String.valueOf(volunteer.getUserId()));
            mUnratedVolunteers.remove(mUnratedVolunteers.indexOf(volunteer.getUserId()));
        }
    }
}