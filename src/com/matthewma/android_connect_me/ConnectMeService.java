package com.matthewma.android_connect_me;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ConnectMeService extends Service implements OnGestureListener{
	  private Looper mServiceLooper;
	  HandlerThread thread;
	  
	  // Handler that receives messages from the thread
	  Button mButton;
	  GestureDetector myGesture;
	  WindowManager wm;
	  
	  @Override
	  public void onCreate() {
		  mButton = new Button(this);
//		  mButton.setText("B");
//		  ViewGroup.LayoutParams params = mButton.getLayoutParams();
//		    //Button new width
//		    params.height = 10;
//
//		    mButton.setLayoutParams(params);
//		  mButton.setLayoutParams (new ViewGroup.LayoutParams(10, 10));
//		  mButton.setLayoutParams(new LinearLayout.LayoutParams(10, 100));
		  mButton.setVisibility(View.VISIBLE);
		  mButton.setBackgroundColor(Color.TRANSPARENT);
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		  Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
	      // For each start request, send a message to start a job and deliver the
	      // start ID so we know which request we're stopping when we finish the job
//		  WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//	                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
//	                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//	                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//	                 PixelFormat.TRANSLUCENT);
		  WindowManager.LayoutParams params = new WindowManager.LayoutParams(
	                200, 25,
	                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
	                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
	                 PixelFormat.TRANSLUCENT);
	        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
//	        params.setTitle("Load Average");
		  
	        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
	        View myview = inflater.inflate(R.layout.nothing, null);
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
	    Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
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
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
//		Toast.makeText(this, "onFling", Toast.LENGTH_SHORT).show();
//		Log.e("swipe","onFling");
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	i.addCategory(Intent.CATEGORY_HOME);
    	startActivity(i);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
//		Log.e("swipe","onLongPress");
//		Toast.makeText(this, "onLongPress  ", Toast.LENGTH_SHORT).show();
//		Toast.makeText(this, "onLongPress", Toast.LENGTH_SHORT).show();
		// TODO Auto-generated method stub
		
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
		return false;
	}
	  

//	  @Override
//		public boolean onTouch(View v, MotionEvent event) {
//	        Toast.makeText(this,"Overlay button event", Toast.LENGTH_SHORT).show();
//			return false;
//		}

	}
