package com.matthewma.swipe_home;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

public class SettingsActivity extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener,OnPreferenceClickListener {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(this);

		final Intent intent = new Intent(this, SwipeService.class);
		if (isMyServiceRunning()) {
			this.stopService(intent);
		}
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean prefEnable = sharedPrefs.getBoolean("prefEnable", true);
		if (prefEnable) {
			this.startService(intent);
		}
		
		findPreference("prefShare").setOnPreferenceClickListener (this);
		findPreference("prefSwipeup").setOnPreferenceClickListener (this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(!key.equals("prefAutoStart")){
			final Intent intent = new Intent(this, SwipeService.class);
			this.stopService(intent);
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(this);
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
		if(key.equals("prefSwipeup")){
			showDialog(ACTION_SELECT_DIALOG);
		}
		return false;
	}
	
	private final int ACTION_SELECT_DIALOG=1;
//	private final int APP_SELECT_DIALOG=2;
	
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ACTION_SELECT_DIALOG:
			final CharSequence[] items = { 
				getString(R.string.dialog0_none), getString(R.string.dialog0_homebutton),
				getString(R.string.dialog0_recentapp), getString(R.string.dialog0_pullnotification),
				getString(R.string.dialog0_homebutton),getString(R.string.dialog0_backbutton)
			};
			Builder builder0 = new AlertDialog.Builder(this);
			builder0.setTitle(getString(R.string.dialog0_title));
			builder0.setCancelable(true);
//			builder0.setPositiveButton("I agree", new OkOnClickListener());
//			builder0.setNegativeButton("No, no", new CancelOnClickListener());
			builder0.setSingleChoiceItems(items, -1,
                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int item) {
		                    Toast.makeText(getApplicationContext(),
		                            items[item], Toast.LENGTH_SHORT).show();
//		                    if(item==5){
//		                    	showDialog(APP_SELECT_DIALOG);
//		                    }
		                    dialog.cancel();  
		                }
		            });
			AlertDialog dialog0 = builder0.create();
			dialog0.show();
			break;
//		case APP_SELECT_DIALOG:
//			final CharSequence[] apps = { "A", "B",
//                    "C"};
//			Builder builder1 = new AlertDialog.Builder(this);
//			builder1.setTitle("Pick a color");
//			builder1.setCancelable(true);
////			builder1.setPositiveButton("I agree", new OkOnClickListener());
////			builder1.setNegativeButton("No, no", new CancelOnClickListener());
//			builder1.setSingleChoiceItems(apps, -1,
//                    new DialogInterface.OnClickListener() {
//		                public void onClick(DialogInterface dialog, int item) {
//		                    Toast.makeText(getApplicationContext(),
//		                    		apps[item], Toast.LENGTH_SHORT).show();
//		                    dialog.cancel();  
//		                }
//		            });
//			AlertDialog dialog1 = builder1.create();
//			dialog1.show();
//			break;
		}
		return super.onCreateDialog(id);
	}

//	private final class CancelOnClickListener implements
//			DialogInterface.OnClickListener {
//		public void onClick(DialogInterface dialog, int which) {
//			Toast.makeText(getApplicationContext(), "Activity will continue",
//					Toast.LENGTH_LONG).show();
//		}
//	}
//
//	private final class OkOnClickListener implements
//			DialogInterface.OnClickListener {
//		public void onClick(DialogInterface dialog, int which) {
//		}
//	}
}