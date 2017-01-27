package com.ibea.fides.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.ibea.fides.BaseActivity;
import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftSearchActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.zipcodeRadio) RadioButton mZipRadio;
    @Bind(R.id.organizationRadio) RadioButton mOrgRadio;
    @Bind(R.id.searchButton) Button mSearchButton;
    @Bind(R.id.searchTextInput) EditText mSearchInput;
    @Bind(R.id.shiftSearchResultsRecyclerView) RecyclerView mRecyclerView;

    // Tracks which radio button is checked
    private String mSearchParameter;
    private String zipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_search);
        ButterKnife.bind(this);

        mSearchButton.setOnClickListener(this);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.zipcodeRadio:
                if (checked)
                    mSearchParameter = "zip";
                break;
            case R.id.organizationRadio:
                if (checked)
                    mSearchParameter = "org";
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if(view == mSearchButton) {
            zipcode = mSearchInput.getText().toString();

            dbShiftsZip = dbShiftsAvailable.child("zipcode").child(zipcode);

            setUpFirebaseAdapter();
        }
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseShiftSearchAdapter(Shift.class, R.layout.dirty_shift_list_item, DirtyFirebaseShiftViewHolder.class, dbShiftsZip) {
            @Override
            protected void populateViewHolder(DirtyFirebaseShiftViewHolder viewHolder, Shift model, int position) {
                viewHolder.bindShift(model);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }