package com.example.ballstars.game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.ballstars.TranslateSensorValue;

public class Ball extends TranslateSensorValue {
    private final Bitmap ballBitmap;
    private final CordsListener cordsListener;
    private final float START_X, START_Y;
    public static final float RADIUS = 50;
    private boolean firstTime = true;

    public Ball(Activity activity, int skinResource, CordsListener cordsListener, float frameWidth, float frameHeight) {
        super(activity, frameWidth, frameHeight);

        ballBitmap =  Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(activity.getResources(), skinResource),
                (int) RADIUS * 2,
                (int) RADIUS * 2,
                false
        );
        this.cordsListener = cordsListener;

        this.START_X = frameWidth / 2f;
        this.START_Y = frameHeight / 2f;

        x = START_X;
        y = START_Y;

        on = false;
    }
    public void start() {
        if (firstTime) {
            on = true;
            firstTime = false;
        }
    }

    public void resume() {
        on = true;
    }

    public void pause() {
        on = false;
    }
    public void reset() {
        x = START_X; vx = 0;
        y = START_Y; vy = 0;
    }


    public Bitmap getBallBitmap() {
        return ballBitmap;
    }
    @Override
    public void translatedSensorData() {
        cordsListener.onCordChange(x, y);
    }

    public interface CordsListener {
        void onCordChange(float x, float y);
    }
}