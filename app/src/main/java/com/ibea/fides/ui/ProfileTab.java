package com.ibea.fides.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibea.fides.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.ibea.fides.R.id.graph;

/**
 * Created by N8Home on 1/27/17.
 */

public class ProfileTab extends Fragment {

    @Bind(R.id.usernametext) TextView username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_tab, container, false);
        ButterKnife.bind(this, view);

        username.setText("Garrett");

        return view;

    }

}