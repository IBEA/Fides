package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ibea.fides.BaseActivity;
import com.ibea.fides.Constants;
import com.ibea.fides.R;
import com.ibea.fides.adapters.DirtyFirebaseShiftViewHolder;
import com.ibea.fides.utils.RecyclerItemListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftSearchActivity extends BaseActivity implements View.OnClickListener, RecyclerItemListener {
    @Bind(R.id.zipcodeRadio)
    RadioButton mZipRadio;
    @Bind(R.id.organizationRadio)
    RadioButton mOrgRadio;
    @Bind(R.id.submitButton)
    Button mSearchButton;
    @Bind(R.id.searchTextInput)
    EditText mSearchInput;
    @Bind(R.id.shiftSearchResultsRecyclerView)
    RecyclerView mRecyclerView;

    // Tracks which radio button is checked
    private String mSearchParameter;
    private String zipcode;

    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerItemListener mTransfer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_search);
        ButterKnife.bind(this);

        mTransfer = this;

        mSearchButton.setOnClickListener(this);
    }

    @Override
    public void userItemClick(Object data, String view){

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
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
        if (view == mSearchButton) {
            zipcode = mSearchInput.getText().toString();

            setUpFirebaseAdapter();
        }
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, DirtyFirebaseShiftViewHolder>
                (String.class, R.layout.dirty_shift_list_item, DirtyFirebaseShiftViewHolder.class, dbShiftsAvailable.child(Constants.DB_SUBNODE_ZIPCODE).child(zipcode)) {

            @Override
            protected void populateViewHolder(DirtyFirebaseShiftViewHolder viewHolder, String shiftID, int position) {
                viewHolder.bindShift(shiftID, mTransfer);
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
}