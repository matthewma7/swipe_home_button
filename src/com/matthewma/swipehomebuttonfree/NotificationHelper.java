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
        			try{
        				Object sbservice = getSystemService( "statusbar" );
        				Class<?> statusbarManager = Class.forName( "android.app.StatusBarManager" );
        				Method showsb;
        				if (Build.VERSION.SDK_INT >= 17) {
        				    showsb = statusbarManager.getMethod("expandNotificationsPanel");
        				}
        				else {
        				    showsb = statusbarManager.getMethod("expand");
        				}
        				showsb.invoke( sbservice );
        				opened=true;
        			}
        			catch(Exception e){
        				Log.e("swipe", "pull down notification exception");
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
