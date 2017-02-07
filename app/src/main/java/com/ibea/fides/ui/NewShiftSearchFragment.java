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
public class NewShiftSearchFragment extends Fragment {


    public NewShiftSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_shift_search, container, false);
    }

}
