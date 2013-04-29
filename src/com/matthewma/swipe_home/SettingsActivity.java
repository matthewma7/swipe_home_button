package com.matthewma.swipe_home;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

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

	@Override
	public boolean onPreferenceClick(Preference preference) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.pref_share_description));
		sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.matthewma.swipe_home");
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
		return false;
	}
}