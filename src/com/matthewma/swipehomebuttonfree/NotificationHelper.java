package com.matthewma.swipehomebuttonfree;

import java.lang.reflect.Method;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.app.Activity;

public class NotificationHelper extends Activity {
	private Handler mHandler = new Handler();
	
	boolean opened;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_helper);
		mHandler.postDelayed(new Runnable() {
            public void run() {
            	synchronized(this){
        			if(Util.pullNotification(NotificationHelper.this)){
        				opened=true;
        			}
        		}
            }
        }, 100);
		
		
	}
	
	@Override  
	public void onWindowFocusChanged(boolean hasFocus) {  
	    // TODO Auto-generated method stub  
	    super.onWindowFocusChanged(hasFocus);  
	    if(hasFocus&&opened)
	    	finish();
	}
}
