// https://github.com/scottyab/HeartBeatView
// This person scottyab is responsible for the heart animations and controllers
// Huge props, made it easy for me to attach a sensor listener and simulate
//  a person's heartbeat
//
// Happy Valentine's Day February 14 2020
// 💜 Vivek Bhookya @mrvivacious

package com.scottyab.heartbeatview.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.os.Vibrator;
import android.widget.TextView;

import com.scottyab.HeartBeatView;

// Get sensor data

public class MainActivity extends Activity implements SensorEventListener {
    // Vivek
    ScrollView layout;
    private SensorManager sensorManager;
    private Sensor heartRate;

    private long endTime;
    private long halfTime;
    private int bpm;

    private Boolean notBeating = true;

    private int beats = 0;

    // Vibration
    // https://github.com/scottyab/HeartBeatView/pull/2/commits/097edce51f4b9ca631f53b66f1ff853a82f0a01a?file-filters%5B%5D=.java
    public int vibrationMillis = 1000;
    private Vibrator vibrator;


    private static final String TAG = "HeartRateViewSample";
    private HeartBeatView heartbeat2;

    private TextView bpmTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // https://stackoverflow.com/questions/2591036/how-to-hide-the-title-bar-for-an-activity-in-xml-with-existing-custom-theme
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        getPermissions();

        layout = (ScrollView) findViewById(R.id.layout);
        heartbeat2 = (HeartBeatView) findViewById(R.id.heartbeat2);
        bpmTV = (TextView) findViewById(R.id.bpm);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        initSensors();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        String eventType = event.sensor.toString().toLowerCase();
        beats++;

        if (endTime == 0 ) {
            endTime = System.currentTimeMillis() + 12000;
            halfTime = endTime - 7000;
//            endTime = System.currentTimeMillis() + 1000;
        }

        if (System.currentTimeMillis() > halfTime && notBeating) {
            bpmTV.setText("Almost done...");
        }

            if (System.currentTimeMillis() > endTime) {
            Log.d(TAG, "onSensorChanged: bpm = " + (beats * 5));

            // Set the heart rate once and don't turn it off
            //  unless app closed
            if (notBeating) {
                notBeating = false;
                bpm = beats * 6;

                heartbeat2.setDurationBasedOnBPM(bpm);
                bpmTV.setText("BPM: " + bpm + "\n\n\nHappy Valentine's Day!");

                // https://stackoverflow.com/questions/18033260/set-background-color-android/18033320
                layout.setBackgroundColor(Color.parseColor("#ffff4444"));
                heartbeat2.toggle();


                vibrator.vibrate(vibrationMillis);

            }
        }

//        Log.d(TAG, "onSensorChanged: beats updated = " + bpm);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onResume() {
        super.onResume();

        sensorManager.registerListener(this, heartRate, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void initSensors() {
        // Get the heart rate sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null) {
            heartRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
//            Log.d(TAG, "onCreate: OUR HEART " + heartRate);
        }
        else {
            Log.d(TAG, "onCreate: We don't have a heart_rate sensor");
        }
    }

    private void getPermissions() {
        // Request storage permissions if not yet authorized
        // Thank you, https://stackoverflow.com/questions/32635704/android-permission-doesnt-work-even-if-i-have-declared-it
        int PERMISSION_REQUEST_CODE = 1;

        // Request body sensorz
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.BODY_SENSORS)
                    == PackageManager.PERMISSION_DENIED) {

//                Log.d("MainActivity", "permission denied to WRTIE EXTERNAL STORAGE - requesting it");
                String[] permissions = {Manifest.permission.BODY_SENSORS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
    }

}
