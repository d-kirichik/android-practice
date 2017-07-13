package com.example.seven_teen.getsensordata;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.seven_teen.getsensordata.MainActivity.JSON;

/**
 * Created by seven-teen on 13.07.17.
 */

public class SendDataTask extends AsyncTask<JSONObject, Void, String> {

    String url;
    TextView coordTextView;

    SendDataTask(String url, TextView coordTextView) {
        this.url = url;
        this.coordTextView = coordTextView;
    }

    @Override
    protected String doInBackground(JSONObject... jsonObjects) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, jsonObjects[0].toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return Long.valueOf(System.currentTimeMillis()).toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        coordTextView.setText(result);
    }
}
