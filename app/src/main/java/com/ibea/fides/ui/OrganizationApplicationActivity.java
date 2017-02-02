package com.ibea.fides.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrganizationApplicationActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.nameInput) EditText mNameInput;
    @Bind(R.id.roleInput) EditText mRoleInput;
    @Bind(R.id.organizationInput) EditText mOrganizationNameInput;
    @Bind(R.id.addressInput) EditText mAddressInput;
    @Bind(R.id.zipcodeInput) EditText mZipInput;
    @Bind(R.id.einInput) EditText mEinInput;
    @Bind(R.id.descriptionInput) EditText mDescriptionInput;
    @Bind(R.id.submitButton) Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_application);
        ButterKnife.bind(this);

        // Set Click Listener
        mSubmitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Sends email to us claiming an organization
        if (view == mSubmitButton) {
            String orgName = mOrganizationNameInput.getText().toString().trim();
            String ein = mEinInput.getText().toString().trim();
            String userName = mNameInput.getText().toString().trim();
            String address = mAddressInput.getText().toString().trim();
            String zip = mZipInput.getText().toString().trim();
            String description = mDescriptionInput.getText().toString().trim();

            submitOrganization(orgName, ein, userName, address, zip, description);
        }
    }

   public void submitOrganization(String orgName, String ein, String userName, String address, String zip, String description) {
       String uid = mCurrentUser.getUid();
       Organization organization = new Organization(uid, orgName, ein, userName, address, zip, description);
       dbPendingOrganizations.child(uid).setValue(organization);

       Toast.makeText(mContext, "Your Application Has Been Received", Toast.LENGTH_LONG).show();

       Intent intent = new Intent(mContext, LogInActivity.class);
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       startActivity(intent);
       finish();
   }
}
