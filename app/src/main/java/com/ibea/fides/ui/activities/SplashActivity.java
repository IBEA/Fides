package com.ibea.fides.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class SplashActivity extends BaseActivity {
    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Determine if user is already signed in. If so, direct to home page
        mAuth = getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Check if user is in auth database
                if(user != null){
                    //Check to see if user is verified
                    if(user.isEmailVerified()){
                        //Check if user has completed account creation
                        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //User has completed account creation
                                if(dataSnapshot.hasChild(uId)) {
                                    mIsOrganization = dataSnapshot.child(uId).getValue(Boolean.class);
//                                    mIsOrganization = dataSnapshot.child(uId).child("isOrganization").getValue(Boolean.class);
                                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(Constants.KEY_ISORGANIZATION, mIsOrganization).apply();
                                    //User is an organization
                                    if(mIsOrganization){
                                        Intent intent = new Intent(SplashActivity.this, OrganizationProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    //User is a volunteer
                                    } else{
                                        Intent intent = new Intent(SplashActivity.this, VolunteerProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                //User is in auth database, but has not finished account creation
                                } else {
                                    Log.d(TAG, "Current user is not null and is in auth but not db");
                                    redirectToLogin();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    //User is not verified
                    }else{
                        redirectToLogin();
                    }
                //User is null
                }else{
                    Log.d(TAG, "Current user is null");
                    redirectToLogin();
                }
            }
        };
    }

    public void redirectToVerifiedAccountCreation(){
        Intent intent = new Intent(SplashActivity.this, VolunteerOrOrganizationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void redirectToLogin(){
        Intent intent = new Intent(SplashActivity.this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    // Add AuthStateListener on Start
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // Remove AuthStateListener on Activity Stop
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
