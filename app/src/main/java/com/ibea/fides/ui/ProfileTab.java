package com.ibea.fides.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.ibea.fides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

//Fragment that holds Basic Profile Data, Trust, and Total Hours worked -- Garrett

public class ProfileTab extends Fragment {

    @Bind(R.id.usernametext) TextView username;
    @Bind(R.id.totalHours) TextView totalHourstext;
    @Bind(R.id.trustpercent) TextView trustpercent;
    @Bind(R.id.dynamicArcView) DecoView arcView;
    @Bind(R.id.hoursArcView) DecoView hoursArcView;

    private boolean once = false;

    int trustmetric = 75; // Will be changed
    int totalHoursWorked = 18; // Will be changed
    int currentdisplayhours = 0;

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

        arcView.addEvent(new DecoEvent.Builder(trustmetric).setIndex(series1Index).setDelay(2000).build());

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
        hoursArcView.addSeries(new SeriesItem.Builder(Color.rgb(216, 241, 255))
                .setRange(0, 5, 5)
                .setInitialVisibility(false)
                .setLineWidth(100f)
                .build());

//Create data series track
        final SeriesItem seriesItem2 = new SeriesItem.Builder(Color.rgb(188, 231, 255))
                .setRange(0, 5, 0)
                .setLineWidth(100f)
                .setSpinDuration(1000)
                .build();

        final SeriesItem seriesItem3 = new SeriesItem.Builder(Color.rgb(150, 218, 255))
                .setSpinDuration(1000)
                .setShowPointWhenEmpty(false)
                .setRange(0, 5, 0)
                .setLineWidth(100f)
                .build();

        final SeriesItem seriesItem4 = new SeriesItem.Builder(Color.rgb(114, 205, 255))
                .setSpinDuration(1000)
                .setShowPointWhenEmpty(false)
                .setRange(0, 5, 0)
                .setLineWidth(100f)
                .build();

        final SeriesItem seriesItem5 = new SeriesItem.Builder(Color.rgb(61, 131, 244))
                .setSpinDuration(1000)
                .setShowPointWhenEmpty(false)
                .setRange(0, 5, 0)
                .setLineWidth(100f)
                .build();

        final int series2Index = hoursArcView.addSeries(seriesItem2);
        final int series3Index = hoursArcView.addSeries(seriesItem3);
        final int series4Index = hoursArcView.addSeries(seriesItem4);
        final int series5Index = hoursArcView.addSeries(seriesItem5);

        hoursArcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        if(totalHoursWorked > 15){
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series2Index).setDelay(1000).build());
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series3Index).setDelay(1800).build());
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series4Index).setDelay(2700).build());
            hoursArcView.addEvent(new DecoEvent.Builder(totalHoursWorked - 15).setIndex(series5Index).setDelay(3600).build());
        }
        else if(totalHoursWorked > 10){
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series2Index).setDelay(2000).build());
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series3Index).setDelay(3800).build());
            hoursArcView.addEvent(new DecoEvent.Builder(totalHoursWorked - 10).setIndex(series4Index).setDelay(5600).build());
        }
        else if(totalHoursWorked > 5) {
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series2Index).setDelay(2000).build());
            hoursArcView.addEvent(new DecoEvent.Builder(totalHoursWorked - 5).setIndex(series3Index).setDelay(3800).build());
        }
        else{
            hoursArcView.addEvent(new DecoEvent.Builder(totalHoursWorked).setIndex(series2Index).setDelay(2000).build());
        }

        seriesItem2.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {

                totalHourstext.setText(String.valueOf((int) currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        seriesItem3.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                totalHourstext.setText(String.valueOf((int) currentPosition + 5));

            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

               seriesItem4.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                totalHourstext.setText(String.valueOf((int) currentPosition + 10));

            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        seriesItem5.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                totalHourstext.setText(String.valueOf((int) currentPosition + 15));

            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });


        return view;

    }

}