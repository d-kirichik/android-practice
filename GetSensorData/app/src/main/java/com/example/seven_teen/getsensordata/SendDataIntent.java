package com.example.seven_teen.getsensordata;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.seven_teen.getsensordata.MainActivity.JSON;

/**
 * Created by seven-teen on 14.07.17.
 */

class SendDataIntent extends IntentService {

    public static final String ACTION_MYINTENTSERVICE = "SendDataIntentService.RESPONSE";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";

    public SendDataIntent(){
        super("Send Data");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String dataString, url;
        if(intent != null) {
            dataString = intent.getStringExtra("data");
            url = intent.getStringExtra("url");
        } else{
            System.out.println("Empty request");
            return;
        }
        Intent responseIntent = new Intent();
        responseIntent.setAction(ACTION_MYINTENTSERVICE);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, dataString);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            responseIntent.putExtra(EXTRA_KEY_OUT, response.body().string());
            sendBroadcast(responseIntent);
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            responseIntent.putExtra(EXTRA_KEY_OUT,  Long.valueOf(System.currentTimeMillis()).toString());
        }

    }
}
