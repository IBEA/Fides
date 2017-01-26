package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ibea.fides.R;
import com.ibea.fides.adapters.ShiftDetailsPagerAdapter;
import com.ibea.fides.models.Shift;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftDetailsActivity extends AppCompatActivity {
    @Bind(R.id.shiftDetailsViewPager) ViewPager mViewPager;
    private ShiftDetailsPagerAdapter adapterViewPager;
    ArrayList<Shift> mShifts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_details);
        ButterKnife.bind(this);

        mShifts = Parcels.unwrap(getIntent().getParcelableExtra("shifts"));
        int startingPosition = getIntent().getIntExtra("position", 0);

        adapterViewPager = new ShiftDetailsPagerAdapter(getSupportFragmentManager(), mShifts);
        mViewPager.setAdapter(adapterViewPager);
        mViewPager.setCurrentItem(startingPosition);
    }
}
