package com.matthewma.swipehomebuttonfree;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

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
			AlertDialog.Builder builder=new AlertDialog.Builder(context);  
			builder
    		.setTitle(R.string.notice_title)
    		.setIcon(R.drawable.s_launcher_48)
    		.setCancelable(false)
    		.setMessage(R.string.accessibility_tosater)
    		.setPositiveButton(context.getString(R.string.notice_understand),new DialogInterface.OnClickListener() {  
                @Override  
                public void onClick(DialogInterface dialog, int which) {
                	dialog.dismiss();
                }
            });
    		AlertDialog dialog=builder.create();
            Window win=dialog.getWindow();
            win.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
		    //Toast.makeText(context,context.getString(R.string.accessibility_tosater), Toast.LENGTH_SHORT).show();
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
