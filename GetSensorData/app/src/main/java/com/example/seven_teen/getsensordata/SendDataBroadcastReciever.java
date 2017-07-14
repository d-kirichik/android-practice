package com.example.seven_teen.getsensordata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

/**
 * Created by seven-teen on 14.07.17.
 */

class SendDataBroadcastReciever extends BroadcastReceiver {

    private TextView coordTextView;

    SendDataBroadcastReciever(TextView coordTextView){
        this.coordTextView = coordTextView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String result = intent
                .getStringExtra(SendDataIntent.EXTRA_KEY_OUT);
        coordTextView.setText(result);
    }
}
