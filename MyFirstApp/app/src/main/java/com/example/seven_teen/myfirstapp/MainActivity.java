package com.example.seven_teen.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private TextView coordTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordTextView = (TextView)findViewById(R.id.myAwesomeTextView);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        coordTextView.setText(mSensorManager.getSensorList(Sensor.TYPE_ALL).toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float yaw_angle = event.values[0]; //x * sin(theta/2);
        float pitch_angle = event.values[1]; //y * sin(theta/2);
        float roll_angle = event.values[2]; //z * sin(theta/2);
        float cos = event.values[3]; // cos(theta/2);
        coordTextView.setText(yaw_angle + "\n" + pitch_angle + "\n" + roll_angle + "\n" + cos);
    }
}

//httpOK
//1) Сервер 2) Post-method