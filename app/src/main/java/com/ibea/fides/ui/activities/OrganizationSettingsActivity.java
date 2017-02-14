package com.ibea.fides.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrganizationSettingsActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    @Bind(R.id.profilePicImageView) ImageView profilePicImageView;
    @Bind(R.id.organizationNameEditText) EditText organizationNameEditText;
    @Bind(R.id.websiteEditText) EditText websiteEditText;
    @Bind(R.id.contactNameEditText) EditText contactNameEditText;
    @Bind(R.id.streetAddressEditText) EditText streetAddressEditText;
    @Bind(R.id.cityEditText) EditText cityEditText;
    @Bind(R.id.stateSpinner) Spinner stateSpinner;
    @Bind(R.id.zipCodeEditText) EditText zipCodeEditText;
    @Bind(R.id.descriptionEditText) EditText descriptionEditText;
    @Bind(R.id.updateButton) Button updateButton;

    Organization thisOrg;

    String mOrganizationName;
    String mWebsite;
    String mContactName;
    String mStreetAddress;
    String mCity;
    String mState;
    String mZip;
    String mDescription;

    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    StorageReference mImageRef;

    Toast toast;
    View toastView;

    public static final int GET_FROM_GALLERY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_settings);
        ButterKnife.bind(this);

        AutoFill();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);

        profilePicImageView.setOnClickListener(this);
        updateButton.setOnClickListener(this);

        // assign image storage reference variables
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://fides-6faeb.appspot.com");
        mImageRef = mStorageRef.child("images/" + uId + ".jpg");

        mImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .placeholder(R.drawable.avatar_blank)
                        .resize(450,400)
                        .centerCrop()
                        .into(profilePicImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mState = parent.getItemAtPosition(pos).toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = intent.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                profilePicImageView.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] serializedImageFile = baos.toByteArray();

                UploadTask uploadTask = mImageRef.putBytes(serializedImageFile);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void AutoFill() {
        dbCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((Boolean) dataSnapshot.child("isOrganization").getValue()) {
                    dbOrganizations.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            thisOrg = dataSnapshot.getValue(Organization.class);

                            if(thisOrg != null) {
                                if (!thisOrg.getName().equals(""))
                                    organizationNameEditText.setHint(thisOrg.getName());
                                if (thisOrg.getUrl() != null)
                                    if(!thisOrg.getUrl().equals(""))
                                        websiteEditText.setHint(thisOrg.getUrl());
                                if (!thisOrg.getContactName().equals(""))
                                    contactNameEditText.setHint(thisOrg.getContactName());
                                if (!thisOrg.getStreetAddress().equals(""))
                                    streetAddressEditText.setHint(thisOrg.getStreetAddress());
                                if (!thisOrg.getCityAddress().equals(""))
                                    cityEditText.setHint(thisOrg.getCityAddress());

                                String state = thisOrg.getStateAddress();
                                Resources res = getResources();
                                String[] states = res.getStringArray(R.array.states_array);
                                int index = Arrays.asList(states).indexOf(state);
                                stateSpinner.setSelection(index);

                                if (!thisOrg.getZipcode().equals(""))
                                    zipCodeEditText.setHint(thisOrg.getZipcode());
                                if (!thisOrg.getDescription().equals(""))
                                    descriptionEditText.setHint(thisOrg.getDescription());
                            }
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

    public void onClick(View view) {
        if(view == profilePicImageView) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

        }
        else if (view == updateButton){
            boolean updated = false;

            if(!organizationNameEditText.getText().toString().trim().equals("")) {
                updateOrgName();
                updated = true;
            }
            if(!websiteEditText.getText().toString().trim().equals("")) {
                updateWebsite();
                updated = true;
            }
            if(!contactNameEditText.getText().toString().trim().equals("")) {
                updateContactName();
                updated = true;
            }
            if(!streetAddressEditText.getText().toString().trim().equals("")) {
                updateStreetAddress();
                updated = true;
            }
            if(!cityEditText.getText().toString().trim().equals("")) {
                updateCity();
                updated = true;
            }
            if(!stateSpinner.getSelectedItem().equals(thisOrg.getStateAddress())) {
                updateState();
                updated = true;
            }
            if(!zipCodeEditText.getText().toString().trim().equals("")) {
                updateZip();
                updated = true;
            }
            if(!descriptionEditText.getText().toString().trim().equals("")) {
                updateDescription();
                updated = true;
            }

            if(updated) {
                toast = Toast.makeText(mContext, "Profile Updated", Toast.LENGTH_SHORT);
            }
            else {
                toast = Toast.makeText(mContext, "No Changes Made", Toast.LENGTH_SHORT);
            }
            toastView = toast.getView();
            toastView.setBackgroundColor(Color.argb(150,0,0,0));
            toastView.setPadding(30,30,30,30);
            toast.setView(toastView);
            toast.show();
        }
    }



    ///////////////////////////////////////////////////////////////////////
    // DB UPDATERS

    private void updateOrgName() {
        String tempOrgName = organizationNameEditText.getText().toString().trim();
        boolean validInput = isValidOrganizationName(tempOrgName);
        if(!validInput){
            return;
        }

        mOrganizationName = tempOrgName;
        dbOrganizations.child(uId).child("name").setValue(mOrganizationName);
        organizationNameEditText.setHint(organizationNameEditText.getText());
        organizationNameEditText.getText().clear();
    }

    private void updateWebsite() {
        String tempWebsite = websiteEditText.getText().toString().trim();
        boolean validInput = isValidWebsite(tempWebsite);
        if(!validInput){
            return;
        }

        mWebsite = tempWebsite;
        dbOrganizations.child(uId).child("url").setValue(mWebsite);
        websiteEditText.setHint(websiteEditText.getText());
        websiteEditText.getText().clear();
    }

    private void updateContactName() {
        String tempContactName = contactNameEditText.getText().toString().trim();
        boolean validInput = isValidContactName(tempContactName);
        if(!validInput){
            return;
        }

        mContactName = tempContactName;
        dbOrganizations.child(uId).child("contactName").setValue(mContactName);
        contactNameEditText.setHint(contactNameEditText.getText());
        contactNameEditText.getText().clear();
    }

    private void updateStreetAddress() {
        String tempStreetAddress = streetAddressEditText.getText().toString().trim();
        boolean validInput = isValidStreetAddress(tempStreetAddress);
        if (!validInput) {
            return;
        }

        mStreetAddress = tempStreetAddress;
        dbOrganizations.child(uId).child("streetAddress").setValue(mStreetAddress);
        streetAddressEditText.setHint(streetAddressEditText.getText());
        streetAddressEditText.getText().clear();
    }

    private void updateCity() {
        String tempCity = cityEditText.getText().toString().trim();
        boolean validInput = isValidCity(tempCity);
        if (!validInput) {
            return;
        }

        mCity = tempCity;
        dbOrganizations.child(uId).child("cityAddress").setValue(mCity);
        cityEditText.setHint(cityEditText.getText());
        cityEditText.getText().clear();
    }

    private void updateState() {
        mState = stateSpinner.getSelectedItem().toString().trim();
        dbOrganizations.child(uId).child("stateAddress").setValue(mState);
    }

    private void updateZip() {
        String tempZip = zipCodeEditText.getText().toString().trim();
        boolean validInput = isValidZip(tempZip);
        if (!validInput) {
            return;
        }

        mZip = tempZip;
        dbOrganizations.child(uId).child("zipcode").setValue(mZip);
        zipCodeEditText.setHint(zipCodeEditText.getText());
        zipCodeEditText.getText().clear();
    }

    private void updateDescription() {
        String tempDescription = descriptionEditText.getText().toString().trim();
        boolean validInput = isValidDescription(tempDescription);

        if(!validInput){
            return;
        }

        mDescription = descriptionEditText.getText().toString();
        dbOrganizations.child(uId).child("description").setValue(mDescription);
        descriptionEditText.setText(descriptionEditText.getText());
        descriptionEditText.getText().clear();
    }



    ///////////////////////////////////////////////////////////////////////
    // TEXT INPUT VALIDATORS

    private boolean isValidOrganizationName(String data) {
        if (data.equals("")) {
            organizationNameEditText.setError("Organization name required");
            return false;
        }
        return true;
    }

    private boolean isValidWebsite(String data) {
        if (data.equals("")) {
            websiteEditText.setError("Website URL required");
            return false;
        }
        return true;
    }

    private boolean isValidContactName(String data) {
        if (data.equals("")) {
            contactNameEditText.setError("Contact name required");
            return false;
        }
        return true;
    }

    private boolean isValidStreetAddress(String data) {
        if (data.equals("")) {
            streetAddressEditText.setError("Street address required");
            return false;
        }
        return true;
    }

    private boolean isValidCity(String data) {
        if (data.equals("")) {
            cityEditText.setError("City required");
            return false;
        }
        return true;
    }

    private boolean isValidZip(String data) {
        if (data.equals("")) {
            zipCodeEditText.setError("ZIP code required");
            return false;
        }
        return true;
    }

    private boolean isValidDescription(String data) {
        if (data.equals("")) {
            descriptionEditText.setError("Description required");
            return false;
        }
        return true;
    }
}