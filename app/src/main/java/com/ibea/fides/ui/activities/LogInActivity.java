package com.ibea.fides.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LogInActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.emailInput) EditText mEmailInput;
    @Bind(R.id.passwordInput) EditText mPasswordInput;
    @Bind(R.id.logInButton) Button mLogInButton;
    @Bind(R.id.createAccountTextView) TextView mNewAccountButton;

    public SharedPreferences mSharedPreferences;
    public boolean mPastOrganization;

    public boolean isOrganization;

    // Misc
    private ProgressDialog mAuthProgressDialog;

    private String userId;
    private String userEmail;

    Toast toast;
    View toastView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPastOrganization = mSharedPreferences.getBoolean(Constants.KEY_ISORGANIZATION, false);

        // Set Click Listeners
        mNewAccountButton.setOnClickListener(this);
        mLogInButton.setOnClickListener(this);

        // Ready Progress Dialog
        createAuthProgressDialog();
    }

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

                // If email has been used to sign up
                if(task.isSuccessful()) {
                    final FirebaseUser user = mAuth.getCurrentUser();

                    // If user has verified email
                    if (user.isEmailVerified()) {
                        Log.d("LogInActivity ", "Volunteer is verified");

                        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userId = user.getUid();
                                userEmail = user.getEmail();

                                // Check to see if user has finished account creation
                                if (dataSnapshot.hasChild(userId)) {

                                    // Check to see if user is an Organization
                                    Boolean isOrganization = dataSnapshot.child(userId).child("isOrganization").getValue(Boolean.class);
                                    if (isOrganization) {
                                        // Check to see if Organization has been Verified
                                        dbOrganizations.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Boolean isVerifiedOrg = dataSnapshot.hasChild(userId);

                                                if (isVerifiedOrg) {
                                                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(Constants.KEY_ISORGANIZATION, isVerifiedOrg).apply();

                                                    Intent intent = new Intent(LogInActivity.this, OrganizationProfileActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                } else{
                                                    Toast.makeText(mContext, "Thank you. Your account is being verified.", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {}
                                        });
                                    } else {
                                        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(Constants.KEY_ISORGANIZATION, false).apply();
                                        // Volunteer is a Volunteer - Send to Volunteer Main Page
                                        Intent intent = new Intent(LogInActivity.this, VolunteerProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    // User has not finished account creation
                                    Intent intent = new Intent(LogInActivity.this, VolunteerOrOrganizationActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {
                        Toast.makeText(mContext, "Please verify your email", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, "Invalid credentials", Toast.LENGTH_LONG).show();
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