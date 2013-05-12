package com.matthewma.swipe_home;

import java.lang.reflect.Method;



import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;


public class SwipeService extends Service implements OnGestureListener{
	HandlerThread thread;
	SharedPreferences sharedPrefs;
	Button mButton;
	GestureDetector myGesture;
	WindowManager wm;
	Boolean prefSwipeNotification;
	final int ANGLE=25;
	final int XYThreshold=55;
	final int buttonWidth=120;
	int buttonHeight=17;
	int screenHeight;

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefSwipeNotification=sharedPrefs.getBoolean("prefSwipeNotification", true);
		Boolean prefTransparentIcon=sharedPrefs.getBoolean("prefTransparentIcon", true);
		Boolean prefIncreaseSensibility=sharedPrefs.getBoolean("prefIncreaseSensibility", false);
		buttonHeight+=prefIncreaseSensibility?5:0;
		
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this,
		        -1, notificationIntent,
		        PendingIntent.FLAG_CANCEL_CURRENT);
		
		
		Notification.Builder notificationBuilder=new Notification.Builder(this);
		
		notificationBuilder
			.setContentIntent(contentIntent)            
            .setWhen(0)
            .setAutoCancel(false)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
        ;
		if(prefTransparentIcon){
			notificationBuilder
				.setSmallIcon(R.drawable.transparent)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.s_notification))
			;
		}
		else{
			notificationBuilder.setSmallIcon(R.drawable.s_notification);
		}
		
		@SuppressWarnings("deprecation")
		Notification notification=notificationBuilder.getNotification();
		
		if(prefTransparentIcon){
			if (Build.VERSION.SDK_INT >= 16) {
				notification.priority=Notification.PRIORITY_MIN;
			}
		}
		notification.when=Integer.MAX_VALUE;
		startForeground(317, notification);

		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenHeight = size.y;
		
		mButton = new Button(this);
		boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
		if(!isDebuggable){
			mButton.setVisibility(View.VISIBLE);
			mButton.setBackgroundColor(Color.TRANSPARENT);  
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int hight=dp2px(buttonHeight);
		int width=dp2px(buttonWidth);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
			width, hight,
			//WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
			WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
			WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
			WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
			PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER;
		myGesture = new GestureDetector(mButton.getContext(),this);

		mButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Log.e("swipe","onTouch");
				//Toast.makeText(ConnectMeService.this, "onTouch", Toast.LENGTH_SHORT).show();
				myGesture.onTouchEvent(event);
				return true;
				//return false;
			}
		});
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		wm.addView(mButton, params);
//		Toast.makeText(this, "Swipe Home Service Started", Toast.LENGTH_SHORT).show();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		wm.removeView(mButton);
//		Toast.makeText(this, "Swipe Home Service Stopped", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float velocityX,
			float velocityY) {
		//Toast.makeText(this, "onFling", Toast.LENGTH_SHORT).show();
		//Log.e("swipe","onFling");
		Float relativeX=Math.abs(arg0.getX()-arg1.getX());
		Float absoluteX=arg0.getX()-arg1.getX();
		Float relativeY=Math.abs(arg0.getY()-arg1.getY());
		Double angle=Math.atan(relativeX/relativeY)/Math.PI*180;
//		Toast.makeText(this, "relativeY:"+relativeY+" velocityY:"+velocityY, Toast.LENGTH_SHORT).show();
		
		
		if(relativeY<dp2px(20)||(relativeY<dp2px(40)&&velocityY>200)){
			action(sharedPrefs.getString("prefSwipeupdown", "2"));
		}
		else{
			if(prefSwipeNotification&&angle>ANGLE&&(relativeY>dp2px(XYThreshold)||relativeX>dp2px(XYThreshold))){
				if(absoluteX<0){
					action(sharedPrefs.getString("prefSwipeupright", "3"));
				}
				else{
					action(sharedPrefs.getString("prefSwipeupleft", "3"));
				}
			}
			else{
				if(relativeY<screenHeight/5*2){
					action(sharedPrefs.getString("prefSwipeup", "1"));
				}
				else{
					action(sharedPrefs.getString("prefSwipefarup", "1"));
				}
			}
		}

		return false;
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
	
	public int dp2px(int dp){
		Resources r = getResources();
	    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}
	
	public void action(String action){
		if(action.equals("0")){
			return;
		}
		if(action.equals("1")){
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
		}
		if(action.equals("2")){
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
		if(action.equals("3")){
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
			}
			catch(Exception e){
				Log.e("swipe", e.getMessage());
			}
		}
		if(action.length()>1 && action.substring(0, 1).equals("4")){
			String [] splits=action.split("\\|");
			if(splits.length==3){
				try{
					Intent intent = new Intent();
				    PackageManager manager = getPackageManager();
				    intent = manager.getLaunchIntentForPackage(splits[2]);
				    intent.addCategory(Intent.CATEGORY_LAUNCHER);
				    startActivity(intent);
				}
				catch(Exception e){}
			}
		}
//		if(action.equals("5")){
//			return getString(R.string.dialog0_backbutton);
//		}
//		if(key.equals("6")){
//			return getString(R.string.dialog0_backbutton);
//		}
	}

}
