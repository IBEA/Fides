package com.ibea.fides;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

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
        dbShifts = db.child(Constants.FIREBASE_CHILD_SHIFTS);
        dbUsers = db.child(Constants.FIREBASE_CHILD_USERS);
        dbOrganizations = db.child(Constants.FIREBASE_CHILD_ORGANIZATIONS);
        dbTags = db.child(Constants.FIREBASE_CHILD_TAGS);

        // Set auth references
        // There are currently NO PROTECTIONS in place for users not being logged in and on a page other than the login screen.
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}


