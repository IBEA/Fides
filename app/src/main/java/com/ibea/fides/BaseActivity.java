package com.ibea.fides;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibea.fides.ui.HomeActivity;
import com.ibea.fides.ui.LogInActivity;
import com.ibea.fides.ui.ShiftSearchActivity;
import com.ibea.fides.ui.ShiftsCreateActivity;
import com.ibea.fides.ui.UserProfileActivity;
import com.ibea.fides.utils.Universal;

public class BaseActivity extends AppCompatActivity {

    // Database References
    public DatabaseReference db;
    public DatabaseReference dbShifts;
    public DatabaseReference dbUsers;
    public DatabaseReference dbOrganizations;
    public DatabaseReference dbPendingOrganizations;
    public DatabaseReference dbTags;
    public DatabaseReference dbCurrentUser;
    public DatabaseReference dbShiftsAvailable;
    public DatabaseReference dbShiftsPending;

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
        TAG = ">>>>>" + this.getClass().getSimpleName();

        // Set database references
        db = FirebaseDatabase.getInstance().getReference();
        dbShifts = db.child(Constants.DB_NODE_SHIFTS);
        dbUsers = db.child(Constants.DB_NODE_USERS);
        dbOrganizations = db.child(Constants.DB_NODE_ORGANIZATIONS);
        dbPendingOrganizations = db.child(Constants.DB_NODE_APPLICATIONS);
        dbTags = db.child(Constants.DB_NODE_TAGS);
        dbShiftsAvailable = db.child(Constants.DB_NODE_SHIFTSAVAILABLE);
        dbShiftsPending = db.child(Constants.DB_NODE_SHIFTSPENDING);

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

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(BaseActivity.this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        else if (id == R.id.action_shifts){
            Intent intent = new Intent(mContext, ShiftsCreateActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.action_dirty_buttons_page) {
            Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.user_page) {
            Intent intent = new Intent(mContext, UserProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}


