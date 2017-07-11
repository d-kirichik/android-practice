package com.example.seven_teen.getsensordata;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private Sensor mAcceleration;
    private TextView coordTextView;
    private long timeStamp;

    private ArrayList<float[]> rotationQueue = new ArrayList<>();
    private ArrayList<float[]> accelerometerQueue = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordTextView = (TextView)findViewById(R.id.OutputTextView);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //coordTextView.setText(mSensorManager.getSensorList(Sensor.TYPE_ALL).toString());
        //coordTextView.setText(mOrientation.getFifoReservedEventCount() + "\n" + mAcceleration.getFifoReservedEventCount());
        timeStamp = System.currentTimeMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientation, 500000);
        mSensorManager.registerListener(this, mAcceleration, 500000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] mAccelerometerReading = new float[3];
        float[] mRotationReading = new float[4];
        if (event.sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
            System.arraycopy(event.values, 0, mRotationReading,
                    0, mRotationReading.length);
            rotationQueue.add(mRotationReading);
        } else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
            accelerometerQueue.add(mAccelerometerReading);
        }
        long now = System.currentTimeMillis();
        if(now - timeStamp > 3000){
            coordTextView.setText(rotationQueue.get(0)[0] + "\n" + accelerometerQueue.get(0)[0]);
            timeStamp = now;
            rotationQueue.clear();
            accelerometerQueue.clear();
        }
    }
}

//httpOK
//1) Сервер 2) Post-method