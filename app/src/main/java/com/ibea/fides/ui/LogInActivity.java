package com.ibea.fides.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.firebase.auth.FirebaseAuth.*;

public class LogInActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.emailInput) EditText mEmailInput;
    @Bind(R.id.passwordInput) EditText mPasswordInput;
    @Bind(R.id.logInButton) Button mLogInButton;
    @Bind(R.id.newAccountText) TextView mNewAccountButton;

    // Firebase
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthListener;

    // Misc
    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);

        // Determine if user is already signed in. If so, direct to home page
        mAuth = getInstance();
        mAuthListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        // Set Click Listeners
        mNewAccountButton.setOnClickListener(this);
        mLogInButton.setOnClickListener(this);

        // Ready Progress Dialog
        createAuthProgressDialog();
    }

    //!! Plant o move this to BaseActivity to prevent fringe cases !!

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
                    FirebaseUser user = mAuth.getCurrentUser();
                }
                if(!task.isSuccessful()) {
                    Toast.makeText(LogInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Progress Dialog
    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Logging in...");
        mAuthProgressDialog.setCancelable(false);
    }
}