package com.ibea.fides.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.ibea.fides.R;
import com.ibea.fides.models.User;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

//Fragment that holds Basic Profile Data, Trust, and Total Hours worked -- Garrett

public class ProfileForVolunteerFragment extends Fragment {

    private User mUser;

    @Bind(R.id.totalHours) TextView totalHourstext;
    @Bind(R.id.trustpercent) TextView trustpercent;
    @Bind(R.id.dynamicArcView) DecoView arcView;
    @Bind(R.id.hoursArcView) DecoView hoursArcView;

    int trustmetric; // Will be changed
    float totalHoursWorked; // Will be changed

    int circlespeed1 = 1000;
    int circlespeed2 = 400;

    // newInstance constructor for creating fragment with arguments
    public static ProfileForVolunteerFragment newInstance(User user) {

        ProfileForVolunteerFragment fragment = new ProfileForVolunteerFragment();

        Bundle bundle = new Bundle();
        bundle.putString("mUser", "Test");
        fragment.setArguments(bundle);

        Log.d(">>>>>", fragment.getArguments().getString("mUser"));

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_volunteer_profile, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.d(">Found bundle", "!");
//            mUser = bundle.getParcelable("Student");
        }else{
            Log.d(">No bundle", "!");
        }

        Log.d(">USER STATE", String.valueOf(mUser));
//        trustmetric = mUser.getRating();
//
//        totalHoursWorked = (float)mUser.getHours(); //TODO once hours are implemented, return hours

// Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(70f)
                .build());

//Create data series track
        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#F57C00"))
                .setRange(0, 100, 0)
                .setSpinDuration(circlespeed1)
                .setLineWidth(70f)
                .setSpinDuration(circlespeed1)
                .build();

        int series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(circlespeed1)
                .setDuration(circlespeed1)
                .build());

        arcView.addEvent(new DecoEvent.Builder(trustmetric).setIndex(series1Index).setDelay(circlespeed1).build());

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
        hoursArcView.addSeries(new SeriesItem.Builder(Color.parseColor("#E0F2F1"))
                .setRange(0, 5, 5)
                .setInitialVisibility(false)
                .setLineWidth(70f)
                .build());

//Create data series track
        final SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#B2DFDB"))
                .setRange(0, 5, 0)
                .setLineWidth(70f)
                .setSpinDuration(circlespeed2)
                .build();

        final SeriesItem seriesItem3 = new SeriesItem.Builder(Color.parseColor("#80CBC4"))
                .setSpinDuration(circlespeed2)
                .setShowPointWhenEmpty(false)
                .setRange(0, 5, 0)
                .setLineWidth(70f)
                .build();

        final SeriesItem seriesItem4 = new SeriesItem.Builder(Color.parseColor("#4DB6AC"))
                .setSpinDuration(circlespeed2)
                .setShowPointWhenEmpty(false)
                .setRange(0, 5, 0)
                .setLineWidth(70f)
                .build();

        final SeriesItem seriesItem5 = new SeriesItem.Builder(Color.parseColor("#009688"))
                .setSpinDuration(circlespeed2)
                .setShowPointWhenEmpty(false)
                .setRange(0, 5, 0)
                .setLineWidth(70f)
                .build();

        final int series2Index = hoursArcView.addSeries(seriesItem2);
        final int series3Index = hoursArcView.addSeries(seriesItem3);
        final int series4Index = hoursArcView.addSeries(seriesItem4);
        final int series5Index = hoursArcView.addSeries(seriesItem5);

        hoursArcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(circlespeed2)
                .setDuration(circlespeed2)
                .build());

        if (totalHoursWorked > 15) {
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series2Index).setDelay(circlespeed2).build());
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series3Index).setDelay(circlespeed2*2).build());
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series4Index).setDelay(circlespeed2*3).build());
            hoursArcView.addEvent(new DecoEvent.Builder(totalHoursWorked - 15).setIndex(series5Index).setDelay(circlespeed2*4).build());
        } else if (totalHoursWorked > 10) {
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series2Index).setDelay(circlespeed2).build());
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series3Index).setDelay(circlespeed2*2).build());
            hoursArcView.addEvent(new DecoEvent.Builder(totalHoursWorked - 10).setIndex(series4Index).setDelay(circlespeed2*3).build());
        } else if (totalHoursWorked > 5) {
            hoursArcView.addEvent(new DecoEvent.Builder(5).setIndex(series2Index).setDelay(circlespeed2).build());
            hoursArcView.addEvent(new DecoEvent.Builder(totalHoursWorked - 5).setIndex(series3Index).setDelay(circlespeed2*2).build());
        } else {
            hoursArcView.addEvent(new DecoEvent.Builder(totalHoursWorked).setIndex(series2Index).setDelay(circlespeed2).build());
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

        circlespeed1 = 300;
        circlespeed2 = 101;

        return view;
    }

}