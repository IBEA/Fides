package com.ibea.fides.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.widget.Toast.makeText;

public class OrganizationApplicationActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.organizationInput) EditText mOrganizationNameInput;
    @Bind(R.id.emailInput) EditText mEmailInput;
    @Bind(R.id.passwordInput) EditText mPasswordInput;
    @Bind(R.id.passwordConfirmInput) EditText mPasswordConfirmInput;

    @Bind(R.id.submitButton) Button mSubmitButton;
    @Bind(R.id.switchToVolunteerButton) Button mSwitchToVolButton;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private ProgressDialog mAuthProgressDialog;

    String mName;
    String mEmail;
    String mPassword; // Added to putExtra -> second step activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_application);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        createAuthStateListener();

        // Set Click Listener
        mSubmitButton.setOnClickListener(this);
        mSwitchToVolButton.setOnClickListener(this);

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
        // Sends email to us claiming an organization
        if (view == mSubmitButton) {
            createOrganization();
        }
        else if (view == mSwitchToVolButton) {
            Intent intent = new Intent(mContext, CreateAccountActivity.class);
            startActivity(intent);
        }
    }

    public void createOrganization() {
        final String orgName = mOrganizationNameInput.getText().toString().trim();
        String email = mEmailInput.getText().toString().trim();
        String password = mPasswordInput.getText().toString().trim();
        String passwordConfirm = mPasswordConfirmInput.getText().toString().trim();

        // Confirm validity of inputs
        boolean validName = isValidOrgName(orgName);
        boolean validEmail = isValidEmail(email);
        boolean validPassword = isValidPassword(password, passwordConfirm);

        if(!validName || !validEmail || !validPassword) {
            return;
        }


        mName = orgName;
        mEmail = email;
        mPassword = password;

        // Display Progress Dialog
        mAuthProgressDialog.show();

        try {
            Task task = mAuth.fetchProvidersForEmail(mEmail);
            Log.d("<<<<<<<<<<<<<<<", task.toString());

            Intent intent = new Intent(mContext, OrganizationFurtherRegistrationInfoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("orgName", mName);
            intent.putExtra("email", mEmail);
            intent.putExtra("password", mPassword);
            startActivity(intent);
            finish();
        }
        catch (NullPointerException e) {
            Log.e(">>>>>>>>>", e.getMessage());

            Toast toast = makeText(mContext, "Account Creation Failed", Toast.LENGTH_LONG);
            View view = toast.getView();
            view.setBackgroundColor(Color.argb(150,0,0,0));
            view.setPadding(30,30,30,30);
            toast.setView(view);
            toast.show();
        }
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

    private boolean isValidOrgName(String name) {
        if (name.equals("")) {
            mOrganizationNameInput.setError("Please enter your name");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        boolean isGoodEmail = (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if(!isGoodEmail) {
            mEmailInput.setError("Please enter a valid email address");
            return false;
        }
        return isGoodEmail;
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
        mAuthProgressDialog.setMessage("Verifying Account Availability...");
        mAuthProgressDialog.setCancelable(false);
    }
}

