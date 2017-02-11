package com.ibea.fides.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.ibea.fides.R;

/**
 * Created by Garrett on 1/25/2017.
 */

//-- Creates the slideshow that appears when app is run for the first time -- Garrettt

public class IntroActivity extends AppIntro {
    private String userId;
    private String userEmail;
    private String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(IntroSlideFragment.newInstance(R.layout.fragment_intro_slide_welcome));
        addSlide(IntroSlideFragment.newInstance(R.layout.fragment_intro_slide_1_volunteer));
        addSlide(IntroSlideFragment.newInstance(R.layout.fragment_intro_slide_2_volunteer));
        addSlide(IntroSlideFragment.newInstance(R.layout.fragment_intro_slide_3));

        //addSlide(AppIntroFragment.newInstance("Slide title", "Loong description here", R.drawable.common_full_open_on_phone, ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null) ));

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#5D4037"));
        setSeparatorColor(Color.parseColor("#EFEBE9"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Retrieve Intent Package
        userId = getIntent().getStringExtra("userId");
        userEmail = getIntent().getStringExtra("userEmail");
        userName = getIntent().getStringExtra("userName");


        setFadeAnimation();

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(IntroActivity.this, CreateUserAccount.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("userEmail", userEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(IntroActivity.this, CreateUserAccount.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("userEmail", userEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}