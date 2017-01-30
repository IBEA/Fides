package com.ibea.fides.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.ibea.fides.R.layout.fragment_shift;

/**
 * Created by N8Home on 1/27/17.
 */

public class ShiftFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         Get the view from fragment_shift.xml
          View view = inflater.inflate(fragment_shift, container, false);
        return view;
    }


}
