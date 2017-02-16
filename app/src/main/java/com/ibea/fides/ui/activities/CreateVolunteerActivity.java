package com.ibea.fides.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.models.Volunteer;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

//No need to extend BaseActivity
public class CreateVolunteerActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.editText_Name) EditText mEditText_Name;
    @Bind(R.id.editText_City) EditText mEditText_City;
    @Bind(R.id.editText_State) EditText mEditText_State;
    @Bind(R.id.editText_Zip) EditText mEditText_Zip;
    @Bind(R.id.button_Submit) FloatingActionButton mButton_Submit;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference dbRef;

    private Volunteer mVolunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_volunteer);
        ButterKnife.bind(this);

        dbRef = FirebaseDatabase.getInstance().getReference();

        mButton_Submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mButton_Submit){
            if(isValid(mEditText_Name) && isValid(mEditText_City) && validateZip(mEditText_Zip)){
                createVolunteer();
                createUser();

                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constants.KEY_ISORGANIZATION, false).apply();
                redirectToVolunteerProfile();
            }
        }
    }

    public void redirectToVolunteerProfile(){
        Intent intent = new Intent(this, VolunteerProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("volunteer", Parcels.wrap(mVolunteer));
        startActivity(intent);
        finish();
    }

    //Create volunteer
    public void createVolunteer(){
        String name = mEditText_Name.getText().toString().trim();
        String city = mEditText_City.getText().toString().trim();
        String state = mEditText_State.getText().toString().trim();
        String zip = mEditText_Zip.getText().toString().trim();
        String email = mAuth.getCurrentUser().getEmail();
        String pushId = mAuth.getCurrentUser().getUid();

        mVolunteer = new Volunteer(pushId, name, email, zip, city, state);
        dbRef.child(Constants.DB_NODE_VOLUNTEERS).child(pushId).setValue(mVolunteer);
    }

    //Create user subnode
    public void createUser(){
        String pushId = mAuth.getCurrentUser().getUid();
        dbRef.child(Constants.DB_NODE_USERS).child(pushId).setValue(false);
    }

    // Validators for inputs
    private boolean isValid(EditText field) {
        String catcher = field.getText().toString().trim();

        if (catcher.equals("")) {
            field.setError("Invalid");
            return false;
        }
        return true;
    }

    public Boolean validateZip(EditText field){
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
}
