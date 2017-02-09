package com.ibea.fides.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.widget.Toast.makeText;

public class OrganizationFurtherRegistrationInfoActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    @Bind(R.id.nameInput) EditText mNameInput;
    @Bind(R.id.addressInput) EditText mAddressInput;
    @Bind(R.id.cityInput) EditText mCityInput;
    @Bind(R.id.stateSpinner) Spinner mStateSpinner;
    @Bind(R.id.zipcodeInput) EditText mZipInput;
    @Bind(R.id.urlInput) EditText mUrlInput;
    @Bind(R.id.descriptionInput) EditText mDescriptionInput;
    @Bind(R.id.submitButton) Button mSubmitButton;

    String mOrgName;
    String mEmail;
    String mPassword;
    String mContactName;
    String mAddress;
    String mCity;
    String mState;
    String mZip;
    String mUrl;
    String mDescription;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_further_registration_info);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        createAuthStateListener();

        // Set Click Listener
        mSubmitButton.setOnClickListener(this);

        createAuthProgressDialog();

        // Populate Spinner w/ State abbrevs
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStateSpinner.setAdapter(adapter);
        mStateSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onClick(View view) {
        if (view == mSubmitButton) {
            createOrganization();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mState = parent.getItemAtPosition(pos).toString();
    }



    public void createOrganization() {
        //TODO: Add form validation
        mOrgName = getIntent().getStringExtra("orgName");
        mEmail = getIntent().getStringExtra("email");
        mPassword = getIntent().getStringExtra("password");
        mContactName = mNameInput.getText().toString().trim();
        mAddress = mAddressInput.getText().toString().trim();
        mCity = mCityInput.getText().toString().trim();
        mZip = mZipInput.getText().toString().trim();
        mUrl = mUrlInput.getText().toString().trim();
        mDescription = mDescriptionInput.getText().toString().trim();

        Log.d("Justin Email:", mEmail);
        Log.d("Justin Password:", mPassword);
        // Display Progress Dialog
        mAuthProgressDialog.show();

        // Upload user authentication info to Firebase
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mAuthProgressDialog.dismiss();

                // If Auth Success, call function passing in User info. If Auth Not Successful, alert.
                if(task.isSuccessful()) {
                    createFirebaseUserProfile(task.getResult().getUser());
                    Intent intent = new Intent(mContext, LogInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast toast = makeText(mContext, "Account Creation Failed", Toast.LENGTH_LONG);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.argb(150,0,0,0));
                    view.setPadding(30,30,30,30);
                    toast.setView(view);
                    toast.show();
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

    // Create Progress Dialog
    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Creating Account...");
        mAuthProgressDialog.setCancelable(false);
    }

    // Upload User DisplayName to Firebase
    private void createFirebaseUserProfile(FirebaseUser user) {
        // Add Display Name to User Authentication in Firebase
        Organization newOrg = new Organization(user.getUid(), mOrgName, mContactName, mAddress, mCity, mState, mZip, mDescription);
        newOrg.setContactEmail(mEmail);
        newOrg.setUrl(mUrl);
        dbPendingOrganizations.child(user.getUid()).setValue(newOrg);

        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder().setDisplayName(mContactName).build();

        // Create Toast, overriding background property of activity
        Toast toast = Toast.makeText(mContext, "Your Application Has Been Received", Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundColor(Color.argb(150,0,0,0));
        view.setPadding(30,30,30,30);
        toast.setView(view);
        toast.show();


        user.updateProfile(addProfileName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {}
        });
    }
}
