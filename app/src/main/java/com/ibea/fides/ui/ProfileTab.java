package com.ibea.fides.ui;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.ibea.fides.R;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

//Fragment that holds Basic Profile Data, Trust, and Total Hours worked -- Garrett

public class ProfileTab extends Fragment {

    @Bind(R.id.usernametext) TextView username;
    @Bind(R.id.trustpercent) TextView trustpercent;
    @Bind(R.id.dynamicArcView) DecoView arcView;
    @Bind(R.id.hoursArcView) DecoView hoursArcView;

    private boolean once = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_tab, container, false);
        ButterKnife.bind(this, view);

// Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(100f)
                .build());

//Create data series track
        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 245, 245, 0))
                .setRange(0, 100, 0)
                .setLineWidth(100f)
                .build();

        int series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        arcView.addEvent(new DecoEvent.Builder(75).setIndex(series1Index).setDelay(2000).build());

        final String format = "%.0f%%";

        seriesItem1.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (format.contains("%%")) {
                    float percentFilled = ((currentPosition - seriesItem1.getMinValue()) / (seriesItem1.getMaxValue() - seriesItem1.getMinValue()));
                    trustpercent.setText(String.format(format, percentFilled * 100f));
                } else {
                    trustpercent.setText(String.format(format, currentPosition));
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        // Create background track
        hoursArcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 25, 25)
                .setInitialVisibility(false)
                .setLineWidth(100f)
                .build());

//Create data series track
        final SeriesItem seriesItem2 = new SeriesItem.Builder(Color.argb(255, 245, 245, 0))
                .setRange(0, 25, 0)
                .setLineWidth(100f)
                .setSpinDuration(2000)
                .build();

        final SeriesItem seriesItem3 = new SeriesItem.Builder(Color.argb(255, 150, 150, 0))
                .setRange(0, 25, 0)
                .setSpinDuration(2000)
                .setLineWidth(100f)
                .setInset(new PointF(20f, 20f))
                .build();

        final int series2Index = hoursArcView.addSeries(seriesItem2);
        final int series3Index = hoursArcView.addSeries(seriesItem3);

        hoursArcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        hoursArcView.addEvent(new DecoEvent.Builder(25).setIndex(series2Index).setDelay(2000).build());

        hoursArcView.addEvent(new DecoEvent.Builder(25).setIndex(series3Index).setDelay(4000).build());

        seriesItem2.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (format.contains("%%")) {
                    float percentFilled = ((currentPosition - seriesItem2.getMinValue()) / (seriesItem2.getMaxValue() - seriesItem2.getMinValue()));
                    trustpercent.setText(String.format(format, percentFilled * 100f));

                    //Log.d("percentFilled" , "check" + percentFilled);
                } else {
                    trustpercent.setText(String.format(format, currentPosition));
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        return view;

    }

}