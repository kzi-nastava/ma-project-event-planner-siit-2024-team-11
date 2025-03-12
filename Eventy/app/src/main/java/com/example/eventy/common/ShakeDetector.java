package com.example.eventy.common;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeDetector implements SensorEventListener {
    private static final float SHAKE_THRESHOLD = 15;
    private static final int SHAKE_TIME_MS = 1000;
    private long lastShakeTime;
    private final Runnable shakeCallback;

    public ShakeDetector(Runnable shakeCallback) {
        this.shakeCallback = shakeCallback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        double acceleration = Math.sqrt(x * x + y * y + z * z);
        long now = System.currentTimeMillis();

        if (acceleration > 9.5 && acceleration < 10.1) return;
        if (now - lastShakeTime < 500) return;

        if (acceleration > SHAKE_THRESHOLD && now - lastShakeTime > SHAKE_TIME_MS) {
            lastShakeTime = now;

            if (shakeCallback != null) {
                shakeCallback.run();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
