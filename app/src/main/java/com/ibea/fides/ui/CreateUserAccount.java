package com.ibea.fides.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;


// Create a User Model -- Not Firebase User
public class CreateUserAccount extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    @Bind(R.id.titleText) TextView mTitleText;
    @Bind(R.id.switchTypeButton) Button mSwitchTypeButton;
    @Bind(R.id.contactNameInput) EditText mContactNameInput;
    @Bind(R.id.streetInput) EditText mStreetInput;
    @Bind(R.id.cityInput) EditText mCityInput;
    @Bind(R.id.stateSpinner) Spinner mStateInput;
    @Bind(R.id.zipcodeInput) EditText mZipcodeInput;
    @Bind(R.id.descriptionInput) EditText mDescriptionInput;
    @Bind(R.id.createUserButton) Button mCreateUserButton;

    private boolean isOrg = false;
    private String mContactName;
    private String mStreet;
    private String mCity;
    private String mState;
    private String mZipcode;
    private String mDescription;
    private String userId;
    private String userEmail;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_account);
        ButterKnife.bind(this);

        // Retrieve Intent Package
        userId = getIntent().getStringExtra("userId");
        userEmail = getIntent().getStringExtra("userEmail");
        userName = getIntent().getStringExtra("userName");

        Log.d("CreateUser:", userId);
        Log.d("CreateUser:", userName);
        Log.d("CreateUser", userEmail);
        Log.d("Justin", isOrg + "");

        // State Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStateInput.setAdapter(adapter);

        // Hide Org Inputs
        mContactNameInput.setVisibility(View.INVISIBLE);
        mStreetInput.setVisibility(View.INVISIBLE);
        mDescriptionInput.setVisibility(View.INVISIBLE);

        // Button Click Listeners
        mSwitchTypeButton.setOnClickListener(this);
        mCreateUserButton.setOnClickListener(this);
        mStateInput.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mState = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onClick(View view) {
        if(view == mSwitchTypeButton) {
            if(isOrg) {
                isOrg = false;
                mContactNameInput.setVisibility(View.INVISIBLE);
                mDescriptionInput.setVisibility(View.INVISIBLE);
                mStreetInput.setVisibility(View.INVISIBLE);
                mTitleText.setText("Sign Up As A Volunteer");
                mSwitchTypeButton.setText("Click Here To Sign Up As An Organization");
            } else {
                isOrg = true;
                mSwitchTypeButton.setText("Click Here To Sign Up As A Volunteer");
                mTitleText.setText("Sign Up As An Organization");
                mContactNameInput.setVisibility(View.VISIBLE);
                mDescriptionInput.setVisibility(View.VISIBLE);
                mStreetInput.setVisibility(View.VISIBLE);
            }

            Log.d("Justin", isOrg + "");
        }
        if(view == mCreateUserButton) {
            Log.d("Justin CreateB", isOrg + "");

            mCity = mCityInput.getText().toString().trim();
            mZipcode = mZipcodeInput.getText().toString().trim();

            // Confirm validity of inputs
            boolean validCity = isValid(mCity, mCityInput);
            boolean validZipcode = validateZip(mZipcode);

            if(!validCity || !validZipcode) {
                return;
            }

            User newUser = new User(userId, userName, userEmail);
            newUser.setCity(mCity);
            newUser.setZipcode(mZipcode);
            newUser.setState(mState);

            if(isOrg) {
                mContactName = mContactNameInput.getText().toString().trim();
                mStreet = mStreetInput.getText().toString().trim();
                mDescription = mDescriptionInput.getText().toString().trim();

                // Confirm validity of inputs
                boolean validContact = isValid(mContactName, mContactNameInput);
                boolean validStreet = isValid(mStreet, mStreetInput);
                boolean validDescription = isValid(mDescription, mDescriptionInput);

                if(!validContact || !validDescription || !validStreet) {
                    return;
                }

                newUser.setIsOrganization(true);
                Organization newOrg = new Organization(userId, userName, mContactName, mStreet, mCity, mState, mZipcode, mDescription);

                dbPendingOrganizations.child(userId).setValue(newOrg);
            }

            dbUsers.child(userId).setValue(newUser);

            Intent intent;
            if(isOrg) {
                intent = new Intent(CreateUserAccount.this, LogInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                intent = new Intent(CreateUserAccount.this, MainActivity_Volunteer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    // Validators for inputs
    private boolean isValid(String name, EditText input) {
        if (name.equals("")) {
            input.setError("Please enter a valid input");
            return false;
        }
        return true;
    }

    public Boolean validateZip(String _query){
        String onlyNumbers = "[0-9]+";

        if(_query.length() != 0){
            if(_query.length() == 5 && _query.matches(onlyNumbers)){
                return true;
            }else{
                Toast.makeText(mContext, "Invalid zip code", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else return true;
    }
}
