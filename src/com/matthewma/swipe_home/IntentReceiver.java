package com.matthewma.swipe_home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IntentReceiver extends BroadcastReceiver {   

    @Override
    public void onReceive(Context context, Intent intent) {

     Intent myIntent = new Intent(context, SwipeService.class);
     context.startService(myIntent);
    }
}