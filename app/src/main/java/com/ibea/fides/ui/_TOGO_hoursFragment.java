package com.ibea.fides.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibea.fides.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

// Currently holds a bar graph that will later show hours volunteered -- Garrett

public class _TOGO_hoursFragment extends android.support.v4.app.Fragment {

    @Bind(R.id.graph)
    GraphView graph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab2.xml
        View view = inflater.inflate(R.layout.fragment_hours, container, false);
        ButterKnife.bind(this, view);

        String[] monthName = { "January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December" };

        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];
        cal.add(Calendar.MONTH, -1);
        String prevmonth = monthName[cal.get(Calendar.MONTH)];
        cal.add(Calendar.MONTH, -1);
        String prevmonth2 = monthName[cal.get(Calendar.MONTH)];

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(5);

        graph.addSeries(series);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {prevmonth2, prevmonth, month});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        return view;
    }

}