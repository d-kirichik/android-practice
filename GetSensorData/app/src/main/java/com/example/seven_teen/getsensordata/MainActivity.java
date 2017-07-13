package com.example.seven_teen.getsensordata;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        coordTextView = (TextView)findViewById(R.id.OutputTextView);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
        JSONObject curJSONData = new JSONObject();
        if (event.sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
            System.arraycopy(event.values, 0, mRotationReading, 0, mRotationReading.length);
            curJSONData = formJSON(mRotationReading, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        } else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values, 0, mAccelerometerReading, 0, mAccelerometerReading.length);
            curJSONData = formJSON(mAccelerometerReading, event.sensor.getType());
        }
        SensorQueue.add(curJSONData);
        long now = System.currentTimeMillis();
        if(now - timeStamp > 3000){
            //coordTextView.setText(now + "\n" + timeStamp);
            timeStamp = now;
            JSONObject postReq = formJSON(SensorQueue);
            String res = sendPostRequest(postReq, "http://10.0.2.2:8081/postData");
            coordTextView.setText(res);
            SensorQueue.clear();
        }
    }

    private JSONObject formJSON(float []data, int type){
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
        }
        catch(JSONException e){
                e.printStackTrace();
        }
        return jsonData;
    }

    private JSONObject formJSON(ArrayList<JSONObject> data){
        JSONObject jsonData = new JSONObject();
        try {
            for (JSONObject o : data) {
                jsonData.accumulate("data", o);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonData;
    }

    private String sendPostRequest(JSONObject data, String addr){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, data.toString());
            Request request = new Request.Builder()
                    .url(addr)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        catch (Exception e){
            e.printStackTrace();
            return Long.valueOf(System.currentTimeMillis()).toString();
        }
    }

}
