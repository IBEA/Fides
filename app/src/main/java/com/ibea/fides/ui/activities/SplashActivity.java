package com.ibea.fides.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

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
    private boolean hasUserModel;

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
                if(user != null){
                    Log.d(TAG, "Current user is not null");
                    Log.d(">Current user name", String.valueOf(user.getDisplayName()));
                    if(user.getDisplayName() != null) {
                        Log.d(">>>>>", "getDisplayName is not null");

                        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(">>>>>", "In db call");
                                hasUserModel = dataSnapshot.hasChild(uId);
                                if(hasUserModel) {
                                    Log.d(TAG, "Current user is not null and in auth & db");
                                    mIsOrganization = dataSnapshot.child(uId).child("isOrganization").getValue(Boolean.class);
                                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(Constants.KEY_ISORGANIZATION, mIsOrganization).apply();
                                    if(mIsOrganization){
                                        Log.d(TAG, "Routing to Org Profile");
                                        Intent intent = new Intent(SplashActivity.this, OrganizationProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else{
                                        Log.d(TAG, "Routing to Vol Profile");
                                        Intent intent = new Intent(SplashActivity.this, VolunteerProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    Log.d(TAG, "Current user is not null and is in auth but not db");
                                    redirectToLogin();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else{
                        Log.d(TAG, "Current user is not null and is not in auth");
                        redirectToLogin();
                    }
                }else{
                    Log.d(TAG, "Current user is null");
                    redirectToLogin();
                }
            }
        };
    }

    public void redirectToLogin(){
        Intent intent = new Intent(SplashActivity.this, LogInActivity.class);
        startActivity(intent);
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
