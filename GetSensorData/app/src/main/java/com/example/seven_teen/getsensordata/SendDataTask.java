package com.example.seven_teen.getsensordata;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

class SendDataTask extends AsyncTask<JSONObject, Void, String> {

    private String url;
    private TextView coordTextView;
    private Context context;

    SendDataTask(Context context, String url, TextView coordTextView) {
        this.url = url;
        this.coordTextView = coordTextView;
        this.context = context;
    }

    @Override
    protected void onPreExecute(){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected()){
            throw new RuntimeException("Internet connection is not established!");
        }
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
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            return Long.valueOf(System.currentTimeMillis()).toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        coordTextView.setText(result);
    }
}
