package com.ibea.fides.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibea.fides.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftsCompletedForVolunteersFragment extends Fragment {


    public ShiftsCompletedForVolunteersFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static ShiftsSearchFragment newInstance(int page, String title) {
        ShiftsSearchFragment fragmentFirst = new ShiftsSearchFragment();
        return fragmentFirst;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shifts_completed_for_volunteers, container, false);
    }

}
