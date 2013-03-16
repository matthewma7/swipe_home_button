package com.matthewma.swipe_home;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity{
	
	TextView tv;
	ToggleButton tb;
	EditText et1;
	EditText et2;
	RadioGroup rg;
	RadioButton rb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tb=(ToggleButton) this.findViewById(R.id.toggleButton1);
		tb.setChecked(isMyServiceRunning());
		
		final Intent intent = new Intent(this, SwipeService.class);
//		final Intent intentSecurer = new Intent(this, SwipeServiceSecurer.class);
		
		 
		
		tb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isMyServiceRunning()){
					MainActivity.this.startService(intent);
//					MainActivity.this.startService(intentSecurer);
				}
				else{
					MainActivity.this.stopService(intent);
				}
			}
		});
	}
	

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}


	
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
