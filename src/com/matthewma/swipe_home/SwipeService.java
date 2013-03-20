package com.matthewma.swipe_home;

import java.lang.reflect.Method;



import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


public class SwipeService extends Service implements OnGestureListener{
	  HandlerThread thread;
	  
	  // Handler that receives messages from the thread
	  Button mButton;
	  GestureDetector myGesture;
	  WindowManager wm;
	  Boolean SwipeNotification=false;
	  
	  @Override
	  public void onCreate() {
		  Notification notification = new Notification(
					R.drawable.s_notification, "Swipe Home",
					System.currentTimeMillis());
			Intent notificationIntent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
			notification.setLatestEventInfo(this, "Swipe Home","Replacing the home button", pendingIntent);
			startForeground(317, notification); 
		  
		  
		  mButton = new Button(this);
//		  mButton.setText("B");
//		  ViewGroup.LayoutParams params = mButton.getLayoutParams();
//		    //Button new width
//		  params.height = 10;
//
//		  mButton.setLayoutParams(params);
//		  mButton.setLayoutParams (new ViewGroup.LayoutParams(10, 10));
//		  mButton.setLayoutParams(new LinearLayout.LayoutParams(10, 100));
		  boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
		  if(!isDebuggable){
			  mButton.setVisibility(View.VISIBLE);
			  mButton.setBackgroundColor(Color.TRANSPARENT);  
		  }
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      // For each start request, send a message to start a job and deliver the
	      // start ID so we know which request we're stopping when we finish the job
//		  WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//	                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
//	                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//	                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//	                 PixelFormat.TRANSLUCENT);
		  WindowManager.LayoutParams params = new WindowManager.LayoutParams(
	                230, 30,
//				  	WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,
	                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
	                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
	                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
	                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
	                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
	                PixelFormat.TRANSLUCENT);
	        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
//	        params.setTitle("Load Average");
		  
//	        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//	        View myview = inflater.inflate(R.layout.nothing, null);
	        myGesture = new GestureDetector(mButton.getContext(),this);
	        
	        mButton.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
//					Log.e("swipe","onTouch");
//					Toast.makeText(ConnectMeService.this, "onTouch", Toast.LENGTH_SHORT).show();
					return myGesture.onTouchEvent(event);
//					return false;
				}
			});
	        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	        wm.addView(mButton, params);
	      // If we get killed, after returning from here, restart
	        
	        Toast.makeText(this, "Swipe Home Service Started", Toast.LENGTH_SHORT).show();
	      return START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	      // We don't provide binding, so return null
	      return null;
	  }
	  
	  @Override
	  public void onDestroy() {
		wm.removeView(mButton);
	    Toast.makeText(this, "Swipe Home Service Stopped", Toast.LENGTH_SHORT).show();
	  }

	@Override
	public boolean onDown(MotionEvent arg0) {
//		Log.e("swipe","onDown");
//		Toast.makeText(this, "onDown", Toast.LENGTH_SHORT).show();
//		Intent i = new Intent(Intent.ACTION_MAIN);
//		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    	i.addCategory(Intent.CATEGORY_HOME);
//    	startActivity(i);
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float velocityX,
			float velocityY) {
//		Toast.makeText(this, "onFling", Toast.LENGTH_SHORT).show();
//		Log.e("swipe","onFling");
		Float relativeX=Math.abs(arg0.getX()-arg1.getX());
		Float relativeY=Math.abs(arg0.getY()-arg1.getY());
		Double angle=Math.atan(relativeX/relativeY)/Math.PI*180;
//		Toast.makeText(this, "angle:"+angle, Toast.LENGTH_SHORT).show();

		if(relativeY>30.0){
			if(SwipeNotification&&angle>25.){
				try{
					Object service = this.getSystemService("statusbar");
					Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
					Method expand = statusbarManager.getMethod("expand");
					expand.invoke(service);
				}
				catch(Exception e){
					Log.e("swipe", e.getMessage());
				}
			}
	    	else{
	    		Intent i = new Intent(Intent.ACTION_MAIN);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	i.addCategory(Intent.CATEGORY_HOME);
		    	startActivity(i);
	    	}
		}
		else{
			try{
    			Class<?> ServiceManagerClass = Class.forName("android.os.ServiceManager");
    	        Class<?> ServiceManagerNativeClass = Class.forName("android.os.ServiceManagerNative");
    	        Method getService = ServiceManagerClass.getMethod("getService", new Class[]{String.class});
    	        IBinder localIBinder = (IBinder)getService.invoke(ServiceManagerNativeClass, "statusbar");
    	        Class<?> IStatusBarServiceClass = Class.forName("com.android.internal.statusbar.IStatusBarService").getClasses()[0];
    	        Method asInterface = IStatusBarServiceClass.getMethod("asInterface", new Class[]{IBinder.class});
    	        Object d = asInterface.invoke(null, new Object[]{localIBinder});
    	        Method toggleRecentApps = IStatusBarServiceClass.getMethod("toggleRecentApps", new Class[0]);
    	        toggleRecentApps.invoke(d);
    		}
    		catch(Exception e){
    			Log.e("swipe", e.getMessage());
    		}
		}
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
//		Log.e("swipe","onLongPress");
//		Toast.makeText(this, "onLongPress", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
//		Log.e("swipe","onScroll");
//		Toast.makeText(this, "onScroll", Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
//		Log.e("swipe","onShowPress");
//		Toast.makeText(this, "onShowPress", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
//		Toast.makeText(this, "onSingleTapUp:", Toast.LENGTH_SHORT).show();
		return false;
	}

	}
