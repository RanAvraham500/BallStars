package com.example.ballstars;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class TranslateSensorValue implements SensorEventListener {
    private final SensorManager sensorManager;
    protected boolean on = true;
    protected float x, y, vx, vy, frameWidth, frameHeight;

    public TranslateSensorValue(Activity activity, float frameWidth, float frameHeight) {
        sensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) == null) {
            throw new NullPointerException("No gravity sensor available");
        }
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        vx = 0; vy = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        handleSensorData(sensorEvent.values[1], sensorEvent.values[0]);
        translatedSensorData();
    }
    public abstract void translatedSensorData();

    /**פעולה שמתרחשת כל פעם שיש ערך חיישנים חדש ויודעת לתרגם את הערך למהירות ומיקום בשני הצירים*/
    private void handleSensorData(float ax, float ay) {
        if (on) {
            final float dt = 0.3f;
            final float COEFFICIENT_OF_REFLECTION = 0.5f;

            vx += dt * ax;
            vy += dt * ay;
            x += vx * dt + 0.5 * ax * Math.pow(dt, 2); //height
            y += vy * dt + 0.5 * ay * Math.pow(dt, 2); //width

            // Ball is out of bounds in y dimension
            if (y > frameHeight) {
                y = frameHeight;
                vy = -COEFFICIENT_OF_REFLECTION * vy;
            } else if (y < 0) {
                y = 0;
                vy = -COEFFICIENT_OF_REFLECTION * vy;
            }


            // Ball is out of bounds in x dimension
            if (x > frameWidth) {
                x = frameWidth;
                vx = -COEFFICIENT_OF_REFLECTION * vx;
            } else if (x < 0) {
                x = 0;
                vx = -COEFFICIENT_OF_REFLECTION * vx;
            }
        }
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //no use
    }

    public void register() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
    }
}
