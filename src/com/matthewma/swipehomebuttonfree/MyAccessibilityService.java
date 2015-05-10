package com.matthewma.swipehomebuttonfree;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class MyAccessibilityService extends AccessibilityService {

	static {
		new ComponentName("com.matthewma.swipehomebuttonfree",MyAccessibilityService.class.getName());
	}

	private static MyAccessibilityService instance;
	
	private static MyAccessibilityService getInstanceOrKickToAccessibilitySetting(Context context)
	{
		if(instance!=null){
			return instance;
		}
		else{
			Intent openAccessiblity = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
			openAccessiblity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			context.startActivity(openAccessiblity);
		    Toast.makeText(context,"N'Please enable Swipe Home Button in this setting page", Toast.LENGTH_SHORT).show();
		}
		return null;
	}
	
	@SuppressLint("NewApi")
	public static boolean pullNotification(Context context) {
		if(getInstanceOrKickToAccessibilitySetting(context)!=null){
			return instance.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS);
		}
		return false;
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {}

	@Override
	public void onInterrupt() {}

	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public void onDestroy() {
		super.onDestroy();
		instance = null;
	}

	@SuppressLint("NewApi")
	public static boolean GoBack(Context context) {
		if(getInstanceOrKickToAccessibilitySetting(context)!=null){
			return instance.performGlobalAction(GLOBAL_ACTION_BACK);
		}
		return false;
	}

	

}
