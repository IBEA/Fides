package com.ibea.fides.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;


// Create A Firebase Volunteer Account -- Does Not Create Volunteer Model
public class CreateAccountActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.emailInput) EditText mEmailInput;
    @Bind(R.id.passwordInput) EditText mPasswordInput;
    @Bind(R.id.passwordConfirmInput) EditText mPasswordConfirmInput;
    @Bind(R.id.createAccountButton) Button mCreateAccountButton;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Misc
    private ProgressDialog mAuthProgressDialog;
    private String mEmail;
    private String mPassword;
    private String mPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        // Set Click Listener
        mCreateAccountButton.setOnClickListener(this);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        createAuthStateListener();

        // Ready Progress Dialog
        createAuthProgressDialog();
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

    @Override
    public void onClick(View view) {
        if(view == mCreateAccountButton) {
            createUser();
        }
    }

    // Create New Account
    private void createUser() {
        mEmail = mEmailInput.getText().toString().trim();
        mPassword = mPasswordInput.getText().toString().trim();
        mPasswordConfirm = mPasswordConfirmInput.getText().toString().trim();

        // Confirm validity of inputs
        boolean validEmail = isValidEmail(mEmail);
        boolean validPassword = isValidPassword(mPassword, mPasswordConfirm);

        if(!validEmail || !validPassword) {
            return;
        }

        // Display Progress Dialog
        mAuthProgressDialog.show();

        // Upload user authentication info to Firebase
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mAuthProgressDialog.dismiss();

                // If Auth Success, create a Authentication Volunteer. If Auth Not Successful, alert.
                if(task.isSuccessful()) {
                    createFirebaseUserProfile(task.getResult().getUser());

                    Intent intent;
                    Toast.makeText(mContext, "Verification email sent", Toast.LENGTH_LONG).show();
                    intent = new Intent(CreateAccountActivity.this, LogInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(mContext, "Account creation failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createAuthStateListener() {
        // Create new AuthStateListener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get user info
                FirebaseUser user = firebaseAuth.getCurrentUser();
            }
        };
    }

    // Validators for email, and password inputs
    private boolean isValidEmail(String email) {
        boolean isGoodEmail = (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if(!isGoodEmail) {
            mEmailInput.setError("Please enter a valid email address");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            mPasswordInput.setError("Please create a password containing at least 6 characters");
            return false;
        } else if (!password.equals(confirmPassword)) {
            mPasswordInput.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    // Create Progress Dialog
    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Creating Account...");
        mAuthProgressDialog.setCancelable(false);
    }

    private void createFirebaseUserProfile(FirebaseUser user) {
        // Send Email Verification
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}