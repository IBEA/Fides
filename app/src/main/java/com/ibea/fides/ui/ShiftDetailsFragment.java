package com.ibea.fides.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibea.fides.R;
import com.ibea.fides.models.Shift;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftDetailsFragment extends Fragment {


    public ShiftDetailsFragment() {
        // Required empty public constructor
    }

    public static ShiftDetailsFragment newInstance(Shift shift) {
        ShiftDetailsFragment shiftDetailsFragment = new ShiftDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("shift", Parcels.wrap(shift));
        shiftDetailsFragment.setArguments(args);
        return shiftDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_shift_details, container, false);
    }

}
