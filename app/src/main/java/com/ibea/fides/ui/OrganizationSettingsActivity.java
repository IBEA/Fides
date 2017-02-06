package com.ibea.fides.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.models.Organization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrganizationSettingsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.pictureButton) Button picturebutton;
    @Bind(R.id.userNameButton) Button userbutton;
    @Bind(R.id.addressButton) Button addressbutton;
    @Bind(R.id.blurbButton) Button blurbbutton;
    @Bind(R.id.streetEditText) EditText streetEditText;
    @Bind(R.id.userNameEditText) EditText userEditText;
    @Bind(R.id.cityEditText) EditText cityEditText;
    @Bind(R.id.stateEditText) EditText stateEditText;
    @Bind(R.id.zipEditText) EditText zipEditText;
    @Bind(R.id.blurbEditText) EditText blurbEditText;
    @Bind(R.id.tagEditText) EditText mTagEditText;
    @Bind(R.id.tagButton) Button mTagButton;


    String mStreet;
    String mCity;
    String mState;
    String mZip;
    String mUsername;
    String mBlurb;
    String mTag;

    public static final int GET_FROM_GALLERY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_settings);
        ButterKnife.bind(this);

        picturebutton.setOnClickListener(this);
        userbutton.setOnClickListener(this);
        addressbutton.setOnClickListener(this);
        blurbbutton.setOnClickListener(this);
        mTagButton.setOnClickListener(this);

        dbOrganizations.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Organization editOrg = dataSnapshot.getValue(Organization.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClick(View view) {
        // On Log In Request
        if(view == picturebutton) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
        }
        else if (view == userbutton){

            createNewUsername();
        }
        else if (view == addressbutton){
            createNewAddress();
        }
        else if (view == blurbbutton){
            createNewBlurb();
        }
        else if (view == mTagButton) {
            addTag();
        }
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

        Toast.makeText(mContext, "Address Updated", Toast.LENGTH_SHORT).show();

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

        Toast.makeText(mContext, "Username updated", Toast.LENGTH_SHORT).show();
        dbOrganizations.child(uId).child("name").setValue(username);
    }

    private void createNewBlurb() {
        if (blurbEditText.getText().toString().trim().equals("")) {
            blurbEditText.setError("Please enter your blurb");
        }
        else{
            mBlurb = blurbEditText.getText().toString();
            blurbEditText.getText().clear();
            Toast.makeText(mContext, "Blurb updated", Toast.LENGTH_SHORT).show();

            //This blurb needs to be placed into the database by backend -- Garrett
        }
    }

    private void addTag() {
        if(mTagEditText.getText().toString().equals("")) {
            mTagEditText.setError("Please enter a tag");
        } else  {
            mTag = mTagEditText.getText().toString().trim();
            mTagEditText.getText().clear();
            Toast.makeText(mContext, "Tag Added", Toast.LENGTH_SHORT).show();

            dbOrganizations.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Organization editOrg = dataSnapshot.getValue(Organization.class);
                    editOrg.getTags().add(mTag);
                    dbOrganizations.child(uId).setValue(editOrg);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
            userEditText.setError("Please enter your new username");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
