package com.ibea.fides.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateOrganizationActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    @Bind(R.id.editText_ContactName) EditText mEditText_ContactName;
    @Bind(R.id.editText_OrganizationName) EditText mEditText_OrganizationName;
    @Bind(R.id.editText_StreetAddress) EditText mEditText_StreetAddress;
    @Bind(R.id.editText_City) EditText mEditText_City;
    @Bind(R.id.stateSpinner) Spinner mStateSpinner;
    @Bind(R.id.editText_Zip) EditText mEditText_Zip;
    @Bind(R.id.editText_Description) EditText mEditText_Description;
    @Bind(R.id.editText_Email) EditText mEditText_Email;
    @Bind(R.id.button_Submit) FloatingActionButton mButton_Submit;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference dbRef;

    private Organization mOrganization;
    private String mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_organization);
        ButterKnife.bind(this);

        dbRef = FirebaseDatabase.getInstance().getReference();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.states_array,R.layout.custom_spinner_item_settings);
        adapter.setDropDownViewResource(R.layout.custom_spinner_list_settings);
        mStateSpinner.setAdapter(adapter);
        mStateSpinner.setSelection(0);

        mButton_Submit.setOnClickListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mState = parent.getItemAtPosition(pos).toString();
    }
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View view) {
        if(view == mButton_Submit){
            if(isValid(mEditText_ContactName) && isValid(mEditText_OrganizationName) && isValid(mEditText_StreetAddress)
                    && isValid(mEditText_City) && isValid(mEditText_Zip) && isValid(mEditText_Description)
                    && isValidEmail(mEditText_Email)){
                createPendingOrganization();
                createUser();

                Toast.makeText(this, "Thank you. We'll be in contact with you soon to confirm your non-profit status.", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            }

        }
    }

    public void createPendingOrganization(){
        String contactName = mEditText_ContactName.getText().toString().trim();
        String organizationName = mEditText_OrganizationName.getText().toString().trim();
        String streetAddress = mEditText_StreetAddress.getText().toString().trim();
        String city = mEditText_City.getText().toString().trim();
        mState = mStateSpinner.getSelectedItem().toString(); // Safety against creating org without ever selecting spinner, resulting in NULL state
        String zipCode = mEditText_Zip.getText().toString().trim();
        String description = mEditText_Description.getText().toString().trim();
        String email = mEditText_Email.getText().toString().trim();
        String uId = mAuth.getCurrentUser().getUid();

        mOrganization = new Organization(uId, organizationName, contactName, streetAddress, city, mState, zipCode, description);
        dbRef.child(Constants.DB_NODE_PENDINGORGANIZATIONS).child(uId).setValue(mOrganization);
    }

    // Validators for email, and password inputs
    private boolean isValidEmail(EditText field) {
        String catcher = field.getText().toString().trim();

        boolean isGoodEmail = (catcher != null && android.util.Patterns.EMAIL_ADDRESS.matcher(catcher).matches());
        if(!isGoodEmail) {
            field.setError("Please enter a valid email address");
            return false;
        }
        return true;
    }

    public void redirectToLogin(){
        Intent intent = new Intent(this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("organization", Parcels.wrap(mOrganization));
        startActivity(intent);
        finish();
    }

    private boolean isValid(EditText field) {
        String catcher = field.getText().toString().trim();

        if (catcher.equals("")) {
            field.setError("Invalid");
            return false;
        }
        return true;
    }

    //Create user subnode
    public void createUser(){
        String pushId = mAuth.getCurrentUser().getUid();
        dbRef.child(Constants.DB_NODE_USERS).child(pushId).setValue(true);
    }
}
