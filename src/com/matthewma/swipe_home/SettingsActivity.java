package com.matthewma.swipe_home;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

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
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		final Intent intent = new Intent(this, SwipeService.class);
		this.stopService(intent);
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Boolean prefEnable = sharedPrefs.getBoolean("prefEnable", true);
		if (prefEnable) {
			this.startService(intent);
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
}