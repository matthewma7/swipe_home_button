package com.matthewma.swipehomebuttonfree;

import java.lang.reflect.Method;
import java.util.Date;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class SwipeService extends AccessibilityService implements OnGestureListener{
	HandlerThread thread;
	SharedPreferences sharedPrefs;
	Button mButton;
	GestureDetector myGesture;
	WindowManager wm;
	Boolean prefForceNotification;
	Boolean prefTabNotice;
	final int ANGLE=25;
	final int XYThreshold=65;
	final int buttonWidth=3000;
	int buttonHeight;
	int screenHeight;
	Boolean firstTapNotice=true;
	Date lastTapTime = new Date();
	Vibrator vibrator;
	Boolean swipeVibrate;
	Boolean tapVibrate;

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
//		Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefForceNotification=sharedPrefs.getBoolean("prefForceNotification", false);
		prefTabNotice=sharedPrefs.getBoolean("prefTabNotice", true);
		swipeVibrate=sharedPrefs.getBoolean("prefVibrate", true);
		tapVibrate=sharedPrefs.getBoolean("prefTapVibrate", true);
		Boolean prefTransparentIcon=sharedPrefs.getBoolean("prefTransparentIcon", true);
		int detectAreaHeight=sharedPrefs.getInt("prefDetectAreaHeight", 2);
		AreaHeight areaHeight=new AreaHeight();
		areaHeight.SetHeight(detectAreaHeight,this);
		buttonHeight=areaHeight.Height;
		

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		Intent notificationIntent = new Intent(this, SettingsActivity.class);
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
//				Log.e("swipe","onTouch");
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
	public void onDestroy() {
		wm.removeView(mButton);
//		Toast.makeText(this, "Swipe Home Service Stopped", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float velocityX,
			float velocityY) {
		//Toast.makeText(this, "onFling", Toast.LENGTH_SHORT).show();
		//Log.e("swipe","onFling");
		Float absoluteX=Math.abs(arg0.getX()-arg1.getX());
		Float relativeX=arg0.getX()-arg1.getX();
		Float absoluteY=Math.abs(arg0.getY()-arg1.getY());
		Double angle=Math.atan(absoluteX/absoluteY)/Math.PI*180;
//		Toast.makeText(this, "relativeY:"+relativeY+" velocityY:"+velocityY, Toast.LENGTH_SHORT).show();
//		int count= sharedPrefs.getInt("prefCount", 0);
//		Editor editor = sharedPrefs.edit();
//		if(count>=65535){
//			count=1000;
//		}
//    	editor.putInt("prefCount", ++count);
//    	editor.apply();
//    	Toast.makeText(this, "Count:"+count, Toast.LENGTH_SHORT).show();
    	
		if(absoluteY<dp2px(20)||(absoluteY<dp2px(40)&&velocityY>200)){
			if(absoluteX<dp2px(50)){
				action(sharedPrefs.getString("prefSwipeupdown", "2"));
			}
			else{
//				Toast.makeText(this, relativeX+"", Toast.LENGTH_SHORT).show();
				if(relativeX<0){
					action(sharedPrefs.getString("prefSwipelowright", "2"));
				}
				else{
					action(sharedPrefs.getString("prefSwipelowleft", "2"));
				}
			}
		}
		else{
			if(angle>ANGLE&&(absoluteY>dp2px(XYThreshold)
					||absoluteX>dp2px(XYThreshold))){
				if(relativeX<0){
					action(sharedPrefs.getString("prefSwipeupright", "3"));
				}
				else{
					action(sharedPrefs.getString("prefSwipeupleft", "3"));
				}
			}
			else{
				if(absoluteY<screenHeight/5*2){
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
		if(tapVibrate){
			vibrator.vibrate(35);
		}
		if(prefTabNotice){
			long timeSpan=(new Date().getTime() - lastTapTime.getTime()) / 1000;
			if(timeSpan<2){
				View checkBoxView = View.inflate(this, R.layout.checkbox, null);
				CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
				checkBox.setText(R.string.dialog_tapnotice_checkbox);
				checkBox.setChecked(prefTabNotice);
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						prefTabNotice=isChecked;
						Editor editor = sharedPrefs.edit();
				    	editor.putBoolean("prefTabNotice", isChecked);
				    	editor.commit();
					}
				});
				
				AlertDialog.Builder builder=new AlertDialog.Builder(this);  
				builder
	    		.setTitle(R.string.dialog_tapnotice_title)
	    		.setIcon(R.drawable.s_launcher_48)
//	    		.setCancelable(false)
	    		.setMessage(R.string.dialog_tapnotice)
	    		.setPositiveButton(getString(R.string.notice_understand),new DialogInterface.OnClickListener() {  
	                @Override  
	                public void onClick(DialogInterface dialog, int which) {  
	                }
	            });
				if(!firstTapNotice){
					builder.setView(checkBoxView);
				}
				else{
					firstTapNotice=false;
				}
	    		AlertDialog dialog=builder.create();
	            Window win=dialog.getWindow();
	            win.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	            dialog.show();
			}
			lastTapTime=new Date();
		}
		return false;
	}
	
	public int dp2px(int dp){
		Resources r = getResources();
	    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}
	
	@SuppressLint("NewApi")
	public void action(String action){
		
		if(action.equals("0")){
			return;
		}
		
		if(swipeVibrate){
			vibrator.vibrate(10);
		}
		
		if(action.equals("1")){
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			try{
				startActivity(i);
			}
			catch(ActivityNotFoundException e){
				
			}
		}
		
		if(action.equals("2")){
			synchronized(this){
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
					Log.e("swipe", "recent apps");
				}
			}
		}
		
		if(action.equals("3")){
			if(prefForceNotification){
				Intent dialogIntent = new Intent(getBaseContext(), NotificationHelper.class);
//				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplication().startActivity(dialogIntent);
			}
			else{
				synchronized(this){
					this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
					/*try{
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
						Log.e("swipe", "pull down notification exception");
					}*/
				}
			}
		}
		
		if(action.length()>1 && action.substring(0, 1).equals("5")){
			String [] splits=action.split("\\|");
			if(splits.length==3){
				try{
					Intent intent = new Intent();
				    PackageManager manager = getPackageManager();
				    intent = manager.getLaunchIntentForPackage(splits[2]);
				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//				    intent.addCategory(Intent.CATEGORY_LAUNCHER);
				    startActivity(intent);
				}
				catch(Exception e){}
			}
		}
		
		if(action.equals("4")){
			Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
			synchronized (this) {
	            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
	            sendOrderedBroadcast(i, null);

	            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
	            sendOrderedBroadcast(i, null);
	            
//				i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
//	            sendOrderedBroadcast(i, null);
//	            
//	            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
//	            sendOrderedBroadcast(i, null);
			 }
		}
		
		if(action.length()>1 && action.substring(0, 1).equals("6")){
			String [] splits=action.split("\\|");
			if(splits.length==3){
				try{
					Intent intent = Intent.parseUri(splits[2], 0);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    startActivity(intent);
				}
				catch(Exception e){
				}
			}
		}
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
	}

}
