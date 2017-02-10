package com.ibea.fides.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ibea.fides.BaseActivity;
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
                if(user != null){
                    if(user.getDisplayName() != null) {
                        if(mIsOrganization){
                            Intent intent = new Intent(SplashActivity.this, MainActivity_Organization.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else{
                            Intent intent = new Intent(SplashActivity.this, MainActivity_Volunteer.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        redirectToLogin();
                    }
                }else{
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
