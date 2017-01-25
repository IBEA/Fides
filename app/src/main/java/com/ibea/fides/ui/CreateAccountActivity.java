package com.ibea.fides.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateAccountActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.nameInput) EditText mNameInput;
    @Bind(R.id.emailInput) EditText mEmailInput;
    @Bind(R.id.passwordInput) EditText mPasswordInput;
    @Bind(R.id.passwordConfirmInput) EditText mPasswordConfirmInput;
    @Bind(R.id.createButton) Button mCreateButton;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public DatabaseReference userRef;

    // Misc
    private ProgressDialog mAuthProgressDialog;
    private String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        // Set Click Listener
        mCreateButton.setOnClickListener(this);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        createAuthStateListener();

        // Firebase References
        userRef = FirebaseDatabase.getInstance().getReference(Constants.DB_NODE_USERS);

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
        if(view == mCreateButton) {
            createNewUser();
        }
    }

    // Create New Account
    private void createNewUser() {
        String name = mNameInput.getText().toString().trim();
        String email = mEmailInput.getText().toString().trim();
        String password = mPasswordInput.getText().toString().trim();
        String passwordConfirm = mPasswordConfirmInput.getText().toString().trim();

        // Confirm validity of inputs
        boolean validName = isValidName(name);
        boolean validEmail = isValidEmail(email);
        boolean validPassword = isValidPassword(password, passwordConfirm);

        if(!validName || !validEmail || !validPassword) {
            return;
        }

        // Set name
        mName = name;

        // Display Progress Dialog
        mAuthProgressDialog.show();

        // Upload user authentication info to Firebase
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mAuthProgressDialog.dismiss();

                // If Auth Success, call function passing in User info. If Auth Not Successful, alert.
                if(task.isSuccessful()) {
                    createFirebaseUserProfile(task.getResult().getUser());
                } else {
                    Toast.makeText(mContext, "Account Creation Failed", Toast.LENGTH_LONG).show();
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

                // If user is present, create new User, update Shared Preferences, send them to Account Setup, clear backstack, and destroy this activity
                if(user != null) {
                    // Send user to new intent
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    // Validators for name, email, and password inputs
    private boolean isValidName(String name) {
        if (name.equals("")) {
            mNameInput.setError("Please enter your name");
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
        mAuthProgressDialog.setMessage("Creating Account...");
        mAuthProgressDialog.setCancelable(false);
    }

    // Upload User DisplayName to Firebase
    private void createFirebaseUserProfile(FirebaseUser user) {
        // Construct new User and Add to Users Table
        User newUser = new User(user.getUid(), mName);
        userRef.child(user.getUid()).setValue(newUser);

        // Add Display Name to User Authentication in Firebase
        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder().setDisplayName(mName).build();
        user.updateProfile(addProfileName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {}
        });
    }
}