package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.ibea.fides.R.layout.shift_fragment;

/**
 * Created by N8Home on 1/27/17.
 */

public class ShiftFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         Get the view from shift_fragment.xml
          View view = inflater.inflate(shift_fragment, container, false);
        return view;
    }
}
