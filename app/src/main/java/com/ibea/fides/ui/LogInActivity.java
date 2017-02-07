package com.ibea.fides.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class LogInActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.emailInput) EditText mEmailInput;
    @Bind(R.id.passwordInput) EditText mPasswordInput;
    @Bind(R.id.logInButton) Button mLogInButton;
    @Bind(R.id.newAccountText) TextView mNewAccountButton;

    public SharedPreferences mSharedPreferences;
    public boolean mPastOrganization;

    public boolean isOrganization;

    // Misc
    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPastOrganization = mSharedPreferences.getBoolean(Constants.KEY_ISORGANIZATION, false);

        // Set Click Listeners
        mNewAccountButton.setOnClickListener(this);
        mLogInButton.setOnClickListener(this);

        // Ready Progress Dialog
        createAuthProgressDialog();
    }

    //!! Plan to move this to BaseActivity to prevent fringe cases !!


    @Override
    public void onClick(View view) {
        // On Log In Request
        if(view == mLogInButton) {
            loginWithPassword();
        }
        if(view == mNewAccountButton) {
            Intent intent = new Intent(mContext, CreateAccountActivity.class);
            startActivity(intent);
        }
    }

    // Log In
    private void loginWithPassword() {
        String email = mEmailInput.getText().toString().trim();
        String password = mPasswordInput.getText().toString().trim();

        if(email.equals("")) {
            mEmailInput.setError("Please enter your email");
            return;
        }
        if(password.equals("")) {
            mPasswordInput.setError("Password must be six characters or longer");
            return;
        }

        // Display Progress Dialog
        mAuthProgressDialog.show();

        // Start Authentication
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Remove Progress Dialog on authentication
                mAuthProgressDialog.dismiss();

                if(task.isSuccessful()) {
                    final FirebaseUser user = mAuth.getCurrentUser();

                    Log.d(TAG, user.getUid());
                    dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean isUser = dataSnapshot.hasChild(user.getUid());
                            if(!isUser) {
                                Toast.makeText(LogInActivity.this, "Your Application is Under Review", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LogInActivity.this, LogInActivity.class);
                                startActivity(intent);
                            } else {
                                dbUsers.child(user.getUid()).child("isOrganization").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        isOrganization = dataSnapshot.getValue(Boolean.class);
                                        Log.d(">>>>>", String.valueOf(isOrganization));

                                        //Put isOrganization Boolean into shared preferences.
                                        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(Constants.KEY_ISORGANIZATION, isOrganization).apply();

                                        Intent intent;

                                        if (isOrganization) {
                                            intent = new Intent(LogInActivity.this, MainActivity_Organization.class);
                                        }
                                        else {
                                            intent = new Intent(LogInActivity.this, MainActivity_Volunteer.class);
                                        }

                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        //intent.putExtra("user" , user);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                if(!task.isSuccessful()) {
                    Toast.makeText(LogInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    // Progress Dialog
    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Logging in...");
        mAuthProgressDialog.setCancelable(false);
    }
}