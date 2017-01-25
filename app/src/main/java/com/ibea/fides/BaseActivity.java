package com.ibea.fides;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    public DatabaseReference dbCurrentUser;

    // Auth references
    public FirebaseAuth mAuth;
    public FirebaseUser mCurrentUser;

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
        dbShifts = db.child(Constants.DB_NODE_SHIFTS);
        dbUsers = db.child(Constants.DB_NODE_USERS);
        dbOrganizations = db.child(Constants.DB_NODE_ORGANIZATIONS);
        dbTags = db.child(Constants.DB_NODE_TAGS);

        // Set auth references
        // There are currently NO PROTECTIONS in place for users not being logged in and on a page other than the login screen.
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        // Check to see if a user is logged in, and ser dbReference.
        if(mCurrentUser != null){
            dbCurrentUser = dbUsers.child(mAuth.getCurrentUser().getUid());
            Log.v(TAG, mAuth.getCurrentUser().getEmail());
        }else{
            Log.v(TAG, "No user logged in");
        }
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

