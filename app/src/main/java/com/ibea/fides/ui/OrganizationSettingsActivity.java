package com.ibea.fides.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrganizationSettingsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.pictureButton) Button pictureButton;
    @Bind(R.id.userNameButton) Button userButton;
    @Bind(R.id.addressButton) Button addressButton;
    @Bind(R.id.descriptionButton) Button descriptionButton;
    @Bind(R.id.streetEditText) EditText streetEditText;
    @Bind(R.id.userNameEditText) EditText userEditText;
    @Bind(R.id.cityEditText) EditText cityEditText;
    @Bind(R.id.stateEditText) EditText stateEditText;
    @Bind(R.id.zipEditText) EditText zipEditText;
    @Bind(R.id.descriptionEditText) EditText descriptionEditText;
    @Bind(R.id.tagEditText) EditText mTagEditText;
    @Bind(R.id.tagButton) Button mTagButton;
    @Bind(R.id.tempPicture) ImageView tempPicture;


    String mStreet;
    String mCity;
    String mState;
    String mZip;
    String mUsername;
    String mDescription;
    String mTag;

    public static final int GET_FROM_GALLERY = 3;

    // image storage reference variables
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    StorageReference mImageRef;

    Toast toast;
    View toastView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_settings);
        ButterKnife.bind(this);

        // assign image storage reference variables
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://fides-6faeb.appspot.com");
        mImageRef = mStorageRef.child("images/" + uId + ".jpg");

        pictureButton.setOnClickListener(this);
        userButton.setOnClickListener(this);
        addressButton.setOnClickListener(this);
        descriptionButton.setOnClickListener(this);
        mTagButton.setOnClickListener(this);

        dbOrganizations.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Organization editOrg = dataSnapshot.getValue(Organization.class);
                streetEditText.setText(editOrg.getStreetAddress());
                cityEditText.setText(editOrg.getCityAddress());
                stateEditText.setText(editOrg.getStateAddress());
                zipEditText.setText(editOrg.getZipcode());
                userEditText.setText(editOrg.getName());
                descriptionEditText.setText(editOrg.getDescription());
                
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClick(View view) {
        // On Log In Request
        if(view == pictureButton) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
        }
        else if (view == userButton){

            createNewUsername();
        }
        else if (view == addressButton){
            createNewAddress();
        }
        else if (view == descriptionButton){
            createNewDescription();
        }
//        else if (toastView == mTagButton) {
//            addTag();
//        }
    }

    private void createNewAddress() {
        String street = streetEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String state = stateEditText.getText().toString().trim();
        String zip = zipEditText.getText().toString().trim();

        // Confirm validity of inputs
        boolean validStreet = isValidStreet(street);
        boolean validCity = isValidCity(city);
        boolean validState = isValidState(state);
        boolean validPassword = isValidPassword(zip);

        if (!validStreet || !validCity || !validPassword || !validState ) {
            return;
        }

        // Set name
        mStreet = street;
        mCity = city;
        mState = state;
        mZip = zip;

        streetEditText.getText().clear();
        cityEditText.getText().clear();
        stateEditText.getText().clear();
        zipEditText.getText().clear();

        toast = Toast.makeText(mContext, "Address Updated", Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.setBackgroundColor(Color.argb(150,0,0,0));
        toastView.setPadding(30,30,30,30);
        toast.setView(toastView);
        toast.show();

        dbOrganizations.child(uId).child("streetAddress").setValue(street);
        dbOrganizations.child(uId).child("cityAddress").setValue(city);
        dbOrganizations.child(uId).child("stateAddress").setValue(state);
        dbOrganizations.child(uId).child("zipcode").setValue(zip);

    }

    private void createNewUsername() {
        String username = userEditText.getText().toString().trim();
        boolean validName = isValidUsername(username);

        if(!validName){
            return;
        }

        mUsername = username;

        userEditText.getText().clear();

        toast = Toast.makeText(mContext, "Username updated", Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.setBackgroundColor(Color.argb(150,0,0,0));
        toastView.setPadding(30,30,30,30);
        toast.setView(toastView);
        toast.show();
        dbOrganizations.child(uId).child("name").setValue(username);
    }

    private void createNewDescription() {
        if (descriptionEditText.getText().toString().trim().equals("")) {
            descriptionEditText.setError("Please enter your blurb");
        }
        else{
            mDescription = descriptionEditText.getText().toString();
            descriptionEditText.getText().clear();
            dbOrganizations.child(uId).child("description").setValue(mDescription);
            toast = Toast.makeText(mContext, "Blurb updated", Toast.LENGTH_SHORT);
            toastView = toast.getView();
            toastView.setBackgroundColor(Color.argb(150,0,0,0));
            toastView.setPadding(30,30,30,30);
            toast.setView(toastView);
            toast.show();
        }
    }

    private boolean isValidStreet(String data) {
        if (data.equals("")) {
            streetEditText.setError("Please enter your street");
            return false;
        }
        return true;
    }

    private boolean isValidCity(String data) {
        if (data.equals("")) {
            cityEditText.setError("Please enter your city");
            return false;
        }
        return true;
    }

    private boolean isValidState(String data) {
        if (data.equals("")) {
            stateEditText.setError("Please enter your state");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String data) {
        if (data.equals("")) {
            zipEditText.setError("Please enter your zip code");
            return false;
        }
        return true;
    }

    private boolean isValidUsername(String data) {
        if (data.equals("")) {
            userEditText.setError("Please enter your new mOrgName");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //Detects request codes
        Uri selectedImageUri = intent.getData();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

            tempPicture.setImageBitmap(bitmap);

            // save picture to firebase storage
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

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }




//    private void addTag() {
//        if(mTagEditText.getText().toString().equals("")) {
//            mTagEditText.setError("Please enter a tag");
//        } else  {
//            mTag = mTagEditText.getText().toString().trim();
//            mTagEditText.getText().clear();
//            toast = Toast.makeText(mContext, "Tag Added", Toast.LENGTH_SHORT);
//            toastView = toast.getView();
//            toastView.setBackgroundColor(Color.argb(150,0,0,0));
//            toastView.setPadding(30,30,30,30);
//            toast.setView(toastView);
//            toast.show();
//
//            dbOrganizations.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Organization editOrg = dataSnapshot.getValue(Organization.class);
//                    editOrg.getTags().add(mTag);
//                    dbOrganizations.child(uId).setValue(editOrg);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }




}
