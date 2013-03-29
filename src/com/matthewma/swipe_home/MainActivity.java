package com.matthewma.swipe_home;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity{
	Button buttonStart;
	Button buttonStop;
	TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		buttonStart=(Button)this.findViewById(R.id.button1);
		buttonStop=(Button)this.findViewById(R.id.button2);
		textView=(TextView)this.findViewById(R.id.textView1);
		ActionBar actionBar = getActionBar();
		actionBar.show();

		
		final Intent intent = new Intent(this, SwipeService.class);		
				
		buttonStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isMyServiceRunning()){
					MainActivity.this.startService(intent);
					update(isMyServiceRunning());
				}
			}
		});
		
		buttonStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isMyServiceRunning()){
					MainActivity.this.stopService(intent);
					update(isMyServiceRunning());
				}
			}
		});
	}
	

	@Override
	protected void onResume(){
		super.onResume();
		update(isMyServiceRunning());
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private Boolean _wasServiceRunning;
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		_wasServiceRunning=isMyServiceRunning();
		switch (item.getItemId()) 
		{
			case R.id.menu_settings:case R.id.menu_settings_menubutton:
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
        	final Intent intent = new Intent(this, SwipeService.class);
        	if(_wasServiceRunning){
        		this.stopService(intent);
    			this.startService(intent);
        	}
            break;
        }
    }
	
	private void update(Boolean isServiceRunning){
		buttonStart.setEnabled(!isServiceRunning);
		buttonStop.setEnabled(isServiceRunning);
		if(isServiceRunning){
			textView.setText(R.string.service_running);
			textView.setTextColor(Color.argb(255, 70, 122, 28));
		}
		else{
			textView.setText(R.string.service_not_running);
			textView.setTextColor(Color.argb(255, 168, 0, 0));
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
