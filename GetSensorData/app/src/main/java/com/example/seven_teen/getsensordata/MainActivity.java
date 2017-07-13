package com.example.seven_teen.getsensordata;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import okhttp3.MediaType;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private Sensor mAcceleration;
    private TextView coordTextView;
    private long timeStamp;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private ArrayList<JSONObject> SensorQueue = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordTextView = findViewById(R.id.OutputTextView);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        timeStamp = System.currentTimeMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

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
        JSONObject curJSONData = new JSONObject();
        if (event.sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
            System.arraycopy(event.values, 0, mRotationReading, 0, mRotationReading.length);
            curJSONData = formJSON(mRotationReading, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading, 0, mAccelerometerReading.length);
            curJSONData = formJSON(mAccelerometerReading, event.sensor.getType());
        }
        SensorQueue.add(curJSONData);
        long now = System.currentTimeMillis();
        if (now - timeStamp > 3000) {
            timeStamp = now;
            JSONObject postReq = formJSON(SensorQueue);
            new SendDataTask("http://10.0.2.2:8081/postData", coordTextView).execute(postReq);
            SensorQueue.clear();
        }
    }

    private JSONObject formJSON(float[] data, int type) {
        JSONObject jsonData = new JSONObject();
        try {
            if (type == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
                jsonData.accumulate("x", data[0]);
                jsonData.accumulate("y", data[1]);
                jsonData.accumulate("z", data[2]);
                jsonData.accumulate("cos", data[3]);
            } else if (type == Sensor.TYPE_ACCELEROMETER) {
                jsonData.accumulate("acc_x", data[0]);
                jsonData.accumulate("acc_y", data[1]);
                jsonData.accumulate("acc_z", data[2]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    private JSONObject formJSON(ArrayList<JSONObject> data) {
        JSONObject jsonData = new JSONObject();
        try {
            for (JSONObject o : data) {
                jsonData.accumulate("data", o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonData;
    }
}
