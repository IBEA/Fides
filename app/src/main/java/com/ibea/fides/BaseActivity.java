package com.ibea.fides;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibea.fides.utils.Universal;

public class BaseActivity extends AppCompatActivity {

    // Database References

    public DatabaseReference db;
    public DatabaseReference dbShifts;
    public DatabaseReference dbUsers;
    public DatabaseReference dbOrganizations;
    public DatabaseReference dbTags;

    // For Navigation
    public Context mContext;

    // Universal Functions
    public Universal mUniversal;

    public String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Set Context and TAG for each Activity
        mContext = this;
        TAG = this.getClass().getSimpleName();

        // Set database references
        db = FirebaseDatabase.getInstance().getReference();
        dbShifts = db.child(Constants.FIREBASE_CHILD_SHIFTS);
        dbUsers = db.child(Constants.FIREBASE_CHILD_USERS);
        dbOrganizations = db.child(Constants.FIREBASE_CHILD_ORGANIZATIONS);
        dbTags = db.child(Constants.FIREBASE_CHILD_TAGS);
    }

    // On Start Override
    @Override
    public void onStart() {
        super.onStart();
    }

    // On Stop Override
    @Override
    public void onStop() {
        super.onStop();
    }

}


