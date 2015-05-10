package com.matthewma.swipehomebuttonfree;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

public class Util {
	//public static final int PrefRatingThreshold=150;
	
	
	public static String getDefaultAction(String key){
		if(key.equals("prefSwipeup")){
			return "1";
		}
		if(key.equals("prefSwipeupdown")){
			return "2";
		}
		if(key.equals("prefSwipeupleft")){
			return "3";
		}
		if(key.equals("prefSwipeupright")){
			return "3";
		}
		if(key.equals("prefSwipefarup")){
			return "1";
		}
		if(key.equals("prefSwipelowleft")){
			return "2";
		}
		if(key.equals("prefSwipelowright")){
			return "2";
		}
		return "0";
	}
	
	public static boolean pullNotification(Context context,boolean useAccessibility){
		boolean result=false;
		if(useAccessibility && Build.VERSION.SDK_INT>=16){
			result=MyAccessibilityService.pullNotification(context);
		}
		else{
			try{
				Object sbservice = context.getSystemService( "statusbar" );
				Class<?> statusbarManager = Class.forName( "android.app.StatusBarManager" );
				Method showsb;
					if (Build.VERSION.SDK_INT >= 17) {
					    showsb = statusbarManager.getMethod("expandNotificationsPanel");
					}
					else {
					    showsb = statusbarManager.getMethod("expand");
					}
					showsb.invoke( sbservice );
				result=true;
			}
			catch(Exception e){
				Log.e("swipe", "pull down notification exception try use accessibility");
				if(Build.VERSION.SDK_INT>=16){
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
					Editor editor = sharedPrefs.edit();
					editor.putBoolean("prefUseAccessibility",true);
					editor.commit();
					result=MyAccessibilityService.pullNotification(context);
				}
			}
		}
		return result;
	}
}

class AreaHeight{
	
	public AreaHeight(){
	}
	public int Progress;
	public String Size;
	public int Height;
	public void SetHeight(int progress, Context context){
		Progress=progress;
		switch (progress){
			case 0:
				Height=11;
				Size=context.getString(R.string.pref_areaheight_small);
				break;
			case 1:
				Height=13;
				Size=context.getString(R.string.pref_areaheight_smaller);
				break;
			case 2:
				Height=17;
				Size=context.getString(R.string.pref_areaheight_normal);
				break;
			case 3:
				Height=20;
				Size=context.getString(R.string.pref_areaheight_larger);
				break;
			case 4:
				Height=22;
				Size=context.getString(R.string.pref_areaheight_large);
				break;
		}
	}
}
