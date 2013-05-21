package com.matthewma.swipe_home;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

public class SettingsActivity extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener,OnPreferenceClickListener {
	
	private SharedPreferences sharedPrefs;
	
	public static final String [] swipes={"prefSwipeup","prefSwipeupdown","prefSwipeupleft","prefSwipeupright","prefSwipefarup"};
	public String currentPref; 
	
	private Dialog dialog1;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		sharedPrefs=PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		sharedPrefs.registerOnSharedPreferenceChangeListener(this);

		final Intent intent = new Intent(this, SwipeService.class);
		if (isMyServiceRunning()) {
			this.stopService(intent);
		}
		Boolean prefEnable = sharedPrefs.getBoolean("prefEnable", true);
		if (prefEnable) {
			this.startService(intent);
			
		}
		
		findPreference("prefShare").setOnPreferenceClickListener (this);
		for(int i=0;i<swipes.length;i++){
			Preference pref=findPreference(swipes[i]);
			pref.setOnPreferenceClickListener(this);
			String action = sharedPrefs.getString(swipes[i], Util.getDefaultAction(swipes[i]));
			pref.setSummary(GetActionDescription(action));
		}
		
		getPackages();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(!key.equals("prefAutoStart")){
			final Intent intent = new Intent(this, SwipeService.class);
			this.stopService(intent);
			Boolean prefEnable = sharedPrefs.getBoolean("prefEnable", true);
			if (prefEnable) {
				this.startService(intent);
			}
		}
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (SwipeService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceClick(Preference preference) {
		String key=preference.getKey();
		if(key.equals("prefShare")){
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.pref_share_description));
			sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.matthewma.swipe_home");
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
		}
		for(int i=0;i<swipes.length;i++){
			if(swipes[i].equals(key)){
				currentPref=key;
				showDialog(ACTION_SELECT_DIALOG);
				return false;
			}
		}
		return false;
	}
	
	private final int ACTION_SELECT_DIALOG=1;
	private final int APP_SELECT_DIALOG=2;
	
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		
		
		case ACTION_SELECT_DIALOG:
			final CharSequence[] items = { 
				getString(R.string.dialog0_none), getString(R.string.dialog0_homebutton),
				getString(R.string.dialog0_recentapp), getString(R.string.dialog0_pullnotification),
				getString(R.string.dialog0_nexttrack), getString(R.string.dialog0_customapp)
			};
			
			Builder builder0 = new AlertDialog.Builder(this);
			builder0.setTitle(getString(R.string.dialog0_title));
			builder0.setCancelable(true);
			String action=sharedPrefs.getString(currentPref, Util.getDefaultAction(currentPref));
			int index=Integer.parseInt(action.substring(0, 1));
			builder0.setSingleChoiceItems(items, index,
                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int item) {
//		                    Toast.makeText(getApplicationContext(),items[item], Toast.LENGTH_SHORT).show();
		                	if(item<5){
		                		Editor editor = sharedPrefs.edit();
			                	editor.putString(currentPref, Integer.toString(item));
			                	editor.commit();
			                	findPreference(currentPref).setSummary(GetActionDescription(Integer.toString(item)));
		                	}
		                    if(item==5){
		                    	showDialog(APP_SELECT_DIALOG);
		                    }
		                    dialog.dismiss();  
		                }
		            });
			AlertDialog dialog0 = builder0.create();
			dialog0.show();
			break;
			
			
		case APP_SELECT_DIALOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.dialog1_title));
			ListView appList = new ListView(this);
			ArrayList<PInfo> pInfos=getPackages();
			AppListAdapter appListAdapter = new AppListAdapter(this, appList.getId(),pInfos.toArray(new PInfo[pInfos.size()]));
			appList.setAdapter(appListAdapter);
			appList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					ListView listView=(ListView)arg0;
//					Toast.makeText(getApplicationContext(),listView.getItemAtPosition(arg2).getClass().toString(), Toast.LENGTH_SHORT).show();
					Editor editor = sharedPrefs.edit();
					PInfo pInfo=(PInfo)(listView.getItemAtPosition(arg2));
					/////////////
                	editor.putString(currentPref,        "5|"+pInfo.appName+"|"+pInfo.packageName);
                	/////////////
                	editor.commit();
                	dialog1.dismiss();
                	findPreference(currentPref).setSummary(pInfo.appName);
				}
			});
			builder.setView(appList);
			dialog1 = builder.create();
			dialog1.show();
			break;
		}
		return super.onCreateDialog(id);
	}


	public String GetActionDescription(String action){
		if(action.equals("0")){
			return getString(R.string.dialog0_none);
		}
		if(action.equals("1")){
			return getString(R.string.dialog0_homebutton);
		}
		if(action.equals("2")){
			return getString(R.string.dialog0_recentapp);
		}
		if(action.equals("3")){
			return getString(R.string.dialog0_pullnotification);
		}
		if(action.equals("4")){
			return getString(R.string.dialog0_nexttrack);
		}
		if(action.length()>=1 && action.substring(0, 1).equals(   "5"   )){
			String [] splits=action.split("\\|");
			if(splits.length==3){
				return splits[1];
			}
		}
//		if(key.equals("6")){
//			return getString(R.string.dialog0_backbutton);
//		}
		return "";
	}
	
	
	class PInfo {
	    public String appName = "";
	    public String packageName = "";
	    public String versionName = "";
	    public int versionCode = 0;
	    public Drawable icon;
//	    public void prettyPrint() {
//	        Log.i("swipehome",appName + " " + packageName + " " + versionName + " " + versionCode);
//	    }
	}

	private ArrayList<PInfo> getPackages() {
	    ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
//	    final int max = apps.size();
//	    for (int i=0; i<max; i++) {
//	        apps.get(i).prettyPrint();
//	    }
	    return apps;
	}
	
	private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
	    ArrayList<PInfo> res = new ArrayList<PInfo>(); 
	    Intent main=new Intent(Intent.ACTION_MAIN, null);
	    main.addCategory(Intent.CATEGORY_LAUNCHER);
	    PackageManager pm=getPackageManager();
	    List<ResolveInfo> launchables=pm.queryIntentActivities(main, 0);
	    for(int i=0;i<launchables.size();i++){
	    	PInfo newInfo = new PInfo();
	        newInfo.appName = launchables.get(i).loadLabel(pm).toString();
	        newInfo.packageName = launchables.get(i).activityInfo.packageName;
	        newInfo.icon = launchables.get(i).loadIcon(pm);
	        res.add(newInfo);
	    }
	    return res; 
	}
}