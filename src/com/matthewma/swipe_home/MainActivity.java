package com.matthewma.swipe_home;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity{
	ToggleButton tb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tb=(ToggleButton) this.findViewById(R.id.toggleButton1);
		
		final Intent intent = new Intent(this, SwipeService.class);		
				
		tb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isMyServiceRunning()){
					MainActivity.this.startService(intent);
				}
				else{
					MainActivity.this.stopService(intent);
				}
			}
		});
	}
	

	@Override
	protected void onResume(){
		super.onResume();
		tb.setChecked(isMyServiceRunning());
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
			case R.id.menu_settings:
				Intent i = new Intent(this, SettingsActivity.class);
				startActivityForResult(i, 1);
				break;
		}
		return false;
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case 1:
        	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		    StringBuilder builder = new StringBuilder();
		
		    builder.append("\n Username: "
		            + sharedPrefs.getString("prefUsername", "NULL"));
		
		    builder.append("\n Send report:"
		            + sharedPrefs.getBoolean("prefSendReport", false));
		
		    builder.append("\n Sync Frequency: "
		            + sharedPrefs.getString("prefSyncFrequency", "NULL"));
		
		    Log.e("swipe",builder.toString());
            break;
        }
    }

	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (SwipeService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
