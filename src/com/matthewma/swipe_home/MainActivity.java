package com.matthewma.swipe_home;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity{	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	

	@Override
	protected void onResume(){
		super.onResume();
		Intent i = new Intent(this, SettingsActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(i, 1);
	}
	
	
		
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}
