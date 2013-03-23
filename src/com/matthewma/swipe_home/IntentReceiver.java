package com.matthewma.swipe_home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class IntentReceiver extends BroadcastReceiver {   

    @Override
    public void onReceive(Context context, Intent intent) {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		Boolean prefAutoStart=sharedPrefs.getBoolean("prefAutoStart", false);
		if(prefAutoStart){
			Intent myIntent = new Intent(context, SwipeService.class);
	    	context.startService(myIntent);
		}
    }
}