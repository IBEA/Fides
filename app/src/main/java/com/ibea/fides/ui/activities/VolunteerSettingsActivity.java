package com.ibea.fides.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;
import com.ibea.fides.models.Volunteer;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VolunteerSettingsActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    @Bind(R.id.updateButton) FloatingActionButton updateButton;
    @Bind(R.id.usernameedittext) EditText useredittext;
    @Bind(R.id.cityedittext) EditText cityeedittext;
    @Bind(R.id.stateSpinner) Spinner mStateInput;
    @Bind(R.id.zipedittext) EditText zipedittext;
    @Bind(R.id.tempPicture) ImageView tempPicture;

    String mCity;
    String mState;
    String mZip;
    String mUsername;

    String mFileName;

    Volunteer thisUser;

    boolean pictureClicked = false;

    public static final int GET_FROM_GALLERY = 3;

    // image storage reference variables
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    StorageReference mImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_settings);
        ButterKnife.bind(this);

        autoFill();

        // State Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStateInput.setAdapter(adapter);

        tempPicture.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        mStateInput.setOnItemSelectedListener(this);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += uId + ".jpeg";

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
                        .into(tempPicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        setTitle("Account Settings");
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mState = parent.getItemAtPosition(pos).toString();
    }

    public void autoFill() {
        dbVolunteers.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

                        public void onDataChange(DataSnapshot dataSnapshot) {
                            thisUser = dataSnapshot.getValue(Volunteer.class);
                            useredittext.setText(thisUser.getName());
                            cityeedittext.setText(thisUser.getCity());
                            zipedittext.setText(thisUser.getZipcode());
                            String state = thisUser.getState();

                            Resources res = getResources();
                            String[] states = res.getStringArray(R.array.states_array);
                            int index = Arrays.asList(states).indexOf(state);


                            mStateInput.setSelection(index);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

    public void onClick(View view) {
        String city = cityeedittext.getText().toString().trim();
        String zip = zipedittext.getText().toString().trim();

        if(view == tempPicture) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
        }
        else if (view == updateButton){

            if( (!useredittext.getText().toString().trim().equals(""))) {
                createNewUsername();
            }

            if( (!zipedittext.getText().toString().trim().equals("")) ||  (!cityeedittext.getText().toString().trim().equals("")) ) {
                createNewAddress();
            }

        }
    }

    private void createNewAddress() {
        String city = cityeedittext.getText().toString().trim();
        String zip = zipedittext.getText().toString().trim();

        // Confirm validity of inputs
        boolean validCity = isValidCity(city);
        boolean validPassword = isValidPassword(zip);

        if ( !validCity || !validPassword ) {
            return;
        }

        // Set name
        mCity = city;
        mZip = zip;

        dbVolunteers.child(uId).child("city").setValue(mCity);
        dbVolunteers.child(uId).child("zipcode").setValue(mZip);
        dbVolunteers.child(uId).child("state").setValue(mState);

        Toast.makeText(mContext, "Address Updated", Toast.LENGTH_SHORT).show();

    }

    private void createNewUsername() {
        String username = useredittext.getText().toString().trim();

        boolean validName = isValidUsername(username);

        if(!validName){
            return;
        }

        mUsername = username;

        dbVolunteers.child(uId).child("name").setValue(mUsername);

        Toast.makeText(mContext, "Username updated", Toast.LENGTH_SHORT).show();

    }

    private boolean isValidCity(String data) {
        if (data.equals("")) {
            cityeedittext.setError("Please enter your city");
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
            useredittext.setError("Please enter your new mOrgName");
            return false;
        }
        return true;
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

                tempPicture.setImageBitmap(bitmap);
                pictureClicked = false;

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

            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


}
