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

import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;

import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrganizationSettingsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.pictureButton) Button picturebutton;
    @Bind(R.id.usernameButton) Button userbutton;
    @Bind(R.id.addressButton) Button addressbutton;
    @Bind(R.id.blurbButton) Button blurbbutton;
    @Bind(R.id.streetedittext) EditText streetedittext;
    @Bind(R.id.usernameedittext) EditText useredittext;
    @Bind(R.id.cityedittext) EditText cityeedittext;
    @Bind(R.id.stateedittext) EditText stateedittext;
    @Bind(R.id.zipedittext) EditText zipedittext;
    @Bind(R.id.blurbeditText) EditText blurbedittext;

    String mStreet;
    String mCity;
    String mState;
    String mZip;
    String mUsername;
    String mBlurb;

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
    }

    private void createNewAddress() {
        String street = streetedittext.getText().toString().trim();
        String city = cityeedittext.getText().toString().trim();
        String state = stateedittext.getText().toString().trim();
        String zip = zipedittext.getText().toString().trim();

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

        streetedittext.getText().clear();
        cityeedittext.getText().clear();
        stateedittext.getText().clear();
        zipedittext.getText().clear();

        //This data needs to be placed into the database by backend -- Garrett
        Toast.makeText(mContext, "Address Updated", Toast.LENGTH_SHORT).show();

    }

    private void createNewUsername() {
        String username = useredittext.getText().toString().trim();
        boolean validName = isValidUsername(username);

        if(!validName){
            return;
        }

        mUsername = username;

        useredittext.getText().clear();

        Toast.makeText(mContext, "Username updated", Toast.LENGTH_SHORT).show();

        //This address needs to be placed into the database by backend -- Garrett
    }

    private void createNewBlurb() {
        if (blurbedittext.getText().toString().trim().equals("")) {
            blurbedittext.setError("Please enter your blurb");
        }
        else{
            mBlurb = blurbedittext.getText().toString();
            blurbedittext.getText().clear();
            Toast.makeText(mContext, "Blurb updated", Toast.LENGTH_SHORT).show();

            //This blurb needs to be placed into the database by backend -- Garrett
        }
    }

    private boolean isValidStreet(String data) {
        if (data.equals("")) {
            streetedittext.setError("Please enter your street");
            return false;
        }
        return true;
    }

    private boolean isValidCity(String data) {
        if (data.equals("")) {
            cityeedittext.setError("Please enter your city");
            return false;
        }
        return true;
    }

    private boolean isValidState(String data) {
        if (data.equals("")) {
            stateedittext.setError("Please enter your state");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String data) {
        if (data.equals("")) {
            zipedittext.setError("Please enter your zip code");
            return false;
        }
        return true;
    }

    private boolean isValidUsername(String data) {
        if (data.equals("")) {
            useredittext.setError("Please enter your new username");
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
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
