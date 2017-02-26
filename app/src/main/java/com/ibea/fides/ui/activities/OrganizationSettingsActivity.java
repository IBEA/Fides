package com.ibea.fides.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;
import com.ibea.fides.models.Shift;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    @Bind(R.id.updateButton) FloatingActionButton updateButton;

    Organization thisOrg;

    String mOrganizationName;
    String mWebsite;
    String mContactName;
    String mStreetAddress;
    String mCity;
    String mState;
    String mZip;
    String mDescription;

    // Variables for updating shift-related nodes
    public Map<String, String> shiftIds = new HashMap<>();
    public DatabaseReference dbRef;
    public String mShiftState;
    public String mShiftCity;
    public Shift mShift;


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

        autoFill();

        // State Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.states_array,R.layout.custom_spinner_item_settings);
        adapter.setDropDownViewResource(R.layout.custom_spinner_list_settings);
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

        setTitle("Account Settings");
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

    public void autoFill() {
        dbOrganizations.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisOrg = dataSnapshot.getValue(Organization.class);
                if(thisOrg != null) {
                    if (!thisOrg.getName().equals(""))
                        organizationNameEditText.setText(thisOrg.getName());
                    if (thisOrg.getUrl() != null)
                        if (!thisOrg.getUrl().equals(""))
                            websiteEditText.setText(thisOrg.getUrl());
                    if (!thisOrg.getContactName().equals(""))
                        contactNameEditText.setText(thisOrg.getContactName());
                    if (!thisOrg.getStreetAddress().equals(""))
                        streetAddressEditText.setText(thisOrg.getStreetAddress());
                    if (!thisOrg.getCityAddress().equals(""))
                        cityEditText.setText(thisOrg.getCityAddress());

                    String state = thisOrg.getStateAddress();
                    Resources res = getResources();
                    String[] states = res.getStringArray(R.array.states_array);
                    int index = Arrays.asList(states).indexOf(state);
                    stateSpinner.setSelection(index);

                    if (!thisOrg.getZipcode().equals(""))
                        zipCodeEditText.setText(thisOrg.getZipcode());
                    if (!thisOrg.getDescription().equals(""))
                        descriptionEditText.setText(thisOrg.getDescription());
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
            if(!validateInputs()) {
                return;
            };
            updateOrganization();
            updateNodes();
        }
    }

    private boolean validateInputs() {

        if(!isValid(organizationNameEditText) || !isValid(websiteEditText) || !isValid(contactNameEditText) || !isValid(streetAddressEditText) || !isValid(cityEditText) || !isValid(descriptionEditText)) {
            return false;
        }

        if(!isValidZip(zipCodeEditText)) {
            return false;
        }

        mOrganizationName = organizationNameEditText.getText().toString();
        mWebsite = websiteEditText.getText().toString();
        mContactName = contactNameEditText.getText().toString();
        mState = stateSpinner.getSelectedItem().toString();
        mStreetAddress = streetAddressEditText.getText().toString();
        mCity = cityEditText.getText().toString();
        mDescription = descriptionEditText.getText().toString();
        mZip = zipCodeEditText.getText().toString();

        return true;
    };

    private boolean isValid(EditText field) {
        String catcher = field.getText().toString().trim();

        if (catcher.equals("")) {
            field.setError("Invalid");
            return false;
        }
        return true;
    }

    private void updateOrganization() {
        thisOrg.setName(mOrganizationName);
        thisOrg.setUrl(mWebsite);
        thisOrg.setContactName(mContactName);
        thisOrg.setStreetAddress(mStreetAddress);
        thisOrg.setCityAddress(mCity);
        thisOrg.setDescription(mDescription);
        thisOrg.setZipcode(mZip);
    }



    public Boolean isValidZip(EditText field){
        String onlyNumbers = "[0-9]+";
        String catcher = field.getText().toString().trim();

        if(catcher.length() != 0){
            if(catcher.length() == 5 && catcher.matches(onlyNumbers)){
                return true;
            }else{
                field.setError("Invalid");
                return false;
            }
        }else return true;
    }

    private void updateNodes() {
        String orgId = thisOrg.getPushId();

        // Update Search Node - Organizations Subnode

        dbRef = FirebaseDatabase.getInstance().getReference();
        Toast.makeText(mContext, "Profile updated", Toast.LENGTH_SHORT).show();
        String searchKey = thisOrg.getName().toLowerCase() + "|" + thisOrg.getZipcode() + "|" + thisOrg.getCityAddress().toLowerCase() + "|" + thisOrg.getStateAddress();
        dbRef.child(Constants.DB_NODE_SEARCH).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(orgId).setValue(searchKey);
        dbRef.child(Constants.DB_NODE_ORGANIZATIONS).child(orgId).setValue(thisOrg);

        // Update OrgName for Current Shifts Belonging to Organization
        dbRef.child(Constants.DB_NODE_SHIFTSPENDING).child(Constants.DB_SUBNODE_ORGANIZATIONS).child(orgId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final String shiftId = snapshot.getKey();
                    Log.d("ShiftId", shiftId);

                    //Change the Org name for Shift node
                    dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).child("organizationName").setValue(mOrganizationName);

                    // Retrieve Shift then change Search Value for StateCity subnodes
                    dbRef.child(Constants.DB_NODE_SHIFTS).child(shiftId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mShift = dataSnapshot.getValue(Shift.class);
                            mShiftCity = mShift.getCity();
                            mShiftState = mShift.getState();

                            String extendedStartTime;

                            if(mShift.getStartTime().length() == 4){
                                extendedStartTime = "0" + mShift.getStartTime();
                            }else{
                                extendedStartTime = mShift.getStartTime();
                            }

                            String searchParam = mShift.getStartDate() + "|" + extendedStartTime + "|" + mShift.getOrganizationName().toLowerCase() + "|" + mShift.getZip() + "|";

                            // Change Search Value for StateCity subnodes
                            dbRef.child(Constants.DB_NODE_SHIFTSAVAILABLE).child(Constants.DB_SUBNODE_STATECITY).child(mShiftState).child(mShiftCity.toLowerCase()).child(shiftId).setValue(searchParam);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_organizationtutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

//          TODO: Set up an actual message
            builder.setMessage("This is the Organization Settings Page. You can change information about your organization here.");

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });


            AlertDialog dialog = builder.create();

            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }
}