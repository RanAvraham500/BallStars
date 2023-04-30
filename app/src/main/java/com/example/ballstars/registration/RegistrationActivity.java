package com.example.ballstars.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.ballstars.dialogs.InfoDialog;
import com.example.ballstars.firebase.CommunicateWithFirebase;
import com.example.ballstars.CustomView;
import com.example.ballstars.MoveAccordingToSensors;
import com.example.ballstars.R;
import com.example.ballstars.ReplaceFragment;
import com.google.android.material.tabs.TabLayout;

public class RegistrationActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener, FragmentResultListener {
    private final String EMAIL_KEY = "email", PASSWORD_KEY = "password", USERNAME_KEY = "username";
    private SignInFragment signInFragment;
    private SignUpFragment signUpFragment;
    private LinearLayout llWelcome, llGroup;
    private CustomView<AppCompatButton> btnContinue;
    private CustomView<ImageButton> btnInfo;
    private MoveAccordingToSensors moveAccordingToSensors;
    private boolean sensorStarted = false;
    private CommunicateWithFirebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebase = new CommunicateWithFirebase(this, getSupportFragmentManager());

        signInFragment = SignInFragment.newInstance();
        signUpFragment = SignUpFragment.newInstance();

        TabLayout tabLayout = findViewById(R.id.tableLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Sign In"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));

        tabLayout.addOnTabSelectedListener(this);
        tabLayout.selectTab(tabLayout.getTabAt(0));

        btnContinue = new CustomView<>(this, findViewById(R.id.btnContinue), this);
        btnInfo = new CustomView<>(this, findViewById(R.id.btnInfo), this);

        llWelcome = findViewById(R.id.llWelcome);
        llGroup = findViewById(R.id.llGroup);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveAccordingToSensors = new MoveAccordingToSensors(
                        llGroup,
                        RegistrationActivity.this,
                        llWelcome.getMeasuredWidth() - llGroup.getMeasuredWidth(),
                        llWelcome.getMeasuredHeight() - llGroup.getMeasuredHeight()
                );
                moveAccordingToSensors.register();
                sensorStarted = true;
            }
        }, 200);

        getSupportFragmentManager().setFragmentResultListener("signInData", this, this);
        getSupportFragmentManager().setFragmentResultListener("signUpData", this, this);
        getSupportFragmentManager().setFragmentResultListener("guestData", this, this);
    }
    @Override
    public void onClick(View view) {
        if (btnContinue.isView(view)) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.flContent);
            if (fragment instanceof SignInFragment) {
                signInFragment.sendData();
            }
            else {
                signUpFragment.sendData();
            }
        } else {
            InfoDialog.display(getSupportFragmentManager(), InfoDialog.InfoDialogType.REGISTRATION);
        }
    }
    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        String email, username, password;
        if (requestKey.equals("signInData")) {
            email = result.getString(EMAIL_KEY);
            password = result.getString(PASSWORD_KEY);
            firebase.signIn(email, password);

        } else {
            email = result.getString(EMAIL_KEY);
            username = result.getString(USERNAME_KEY);
            password = result.getString(PASSWORD_KEY);
            firebase.signUp(email, username, password);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        handleOnTabSelected(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        handleOnTabSelected(tab);
    }

    private void handleOnTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0 -> {
                ReplaceFragment.init(getSupportFragmentManager(), signInFragment);
            }
            case 1 -> {
                ReplaceFragment.init(getSupportFragmentManager(), signUpFragment);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sensorStarted) {
            moveAccordingToSensors.register();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sensorStarted) {
            moveAccordingToSensors.unregister();
        }
    }
}