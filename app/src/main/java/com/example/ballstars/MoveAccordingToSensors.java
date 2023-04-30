package com.example.ballstars;

import android.app.Activity;
import android.view.View;

public class MoveAccordingToSensors extends TranslateSensorValue {
    private final View mView;

    public MoveAccordingToSensors(View view, Activity activity, float frameWidth, float frameHeight) {
        super(activity, frameWidth, frameHeight);
        mView = view;

        x = view.getX();
        y = view.getY();
    }

    @Override
    public void translatedSensorData() {
        mView.setX(x);
        mView.setY(y);
    }
}