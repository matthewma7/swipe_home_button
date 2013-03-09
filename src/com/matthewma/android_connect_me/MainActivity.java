package com.matthewma.android_connect_me;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
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
		
		final Intent intent = new Intent(this, ConnectMeService.class);
//		Bundle bundle = new Bundle();
//		bundle.putInt("listenInteval", Integer.parseInt(et1.getText().toString()));
//		bundle.putInt("listenWindowSize", Integer.parseInt(et2.getText().toString()));
//		intent.putExtras(bundle);
		
		
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
	

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}


	
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (ConnectMeService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
