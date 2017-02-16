package com.ibea.fides.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.github.paolorotolo.appintro.AppIntro;
import com.ibea.fides.R;
import com.ibea.fides.ui.fragments.IntroSlideFragment;

/**
 * Created by Garrett on 2/16/2017.
 */

public class IntroVolunteerActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(IntroSlideFragment.newInstance(R.layout.fragment_intro_slide_1_volunteer));
        addSlide(IntroSlideFragment.newInstance(R.layout.fragment_intro_slide_2_volunteer));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#5D4037"));
        setSeparatorColor(Color.parseColor("#EFEBE9"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        setFadeAnimation();

        setTitle("Tutorial");
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
