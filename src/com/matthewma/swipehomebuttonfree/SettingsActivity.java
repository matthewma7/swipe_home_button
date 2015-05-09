package com.matthewma.swipehomebuttonfree;

import java.util.ArrayList;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;


public class SettingsActivity extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener,OnPreferenceClickListener {
	
	private SharedPreferences sharedPrefs;
	
	public static final String [] swipes={"prefSwipeup","prefSwipeupdown","prefSwipeupleft","prefSwipeupright","prefSwipefarup","prefSwipelowleft","prefSwipelowright"};
	
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
		
		findPreference("prefShare").setOnPreferenceClickListener(this);
		findPreference("prefTransparentIcon").setOnPreferenceClickListener(this);
		findPreference("prefDetectAreaProgress").setOnPreferenceClickListener(this);
		for(int i=0;i<swipes.length;i++){
			Preference pref=findPreference(swipes[i]);
			pref.setOnPreferenceClickListener(this);
			String action = sharedPrefs.getString(swipes[i], Util.getDefaultAction(swipes[i]));
			pref.setSummary(GetActionDescription(action));
		}
	}
	
//	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
//		sharedPrefs=PreferenceManager.getDefaultSharedPreferences(this);
//		if(sharedPrefs.getInt("prefCount", 0)>Util.PrefRatingThreshold&&findPreference("prefRating")==null){
//			PreferenceCategory prefCategoryAbout = (PreferenceCategory)findPreference("prefCategoryAbout");
//			Preference prefRating = new Preference(this);
//			prefRating.setKey("prefRating");
//			prefRating.setTitle(getString(R.string.pref_rating));
//			prefRating.setSummary(getString(R.string.pref_rating_summary));
//			prefRating.setOnPreferenceClickListener(this);
//			prefCategoryAbout.addPreference(prefRating);
//			prefRating.setOrder(-1);
//		}
		super.onResume();
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
			try{
				startActivity(sendIntent);
			}
			catch(ActivityNotFoundException e){
				
			}
			return false;
		}
		for(int i=0;i<swipes.length;i++){
			if(swipes[i].equals(key)){
				currentPref=key;
				if(currentPref.equals("prefSwipelowleft")||currentPref.equals("prefSwipelowright")){
					showDialog(LOWER_WARN_NOTICE);
				}
				else{
					showDialog(ACTION_SELECT_DIALOG);
				}
				return false;
			}
		}
		if("prefTransparentIcon".equals(key)&&((CheckBoxPreference)preference).isChecked()){
			showDialog(TRANSPARENT_NOTICE_DIALOG);
			return false;
		}
//		if("prefIncreaseSensibility".equals(key)&&((CheckBoxPreference)preference).isChecked()){
//			showDialog(SENSIBILITY_DIALOG);
//			return false;
//		}
		if("prefRating".equals(key)){
			Intent intent = new Intent(Intent.ACTION_VIEW); 
			intent.setData(Uri.parse("market://details?id=com.matthewma.swipe_home")); 
			try{
				startActivity(intent);
			}
			catch(ActivityNotFoundException e){
				
			}
			return false;
		}
		if("prefDetectAreaProgress".equals(key)){
			showDialog(DETECT_AREA_HEIGHT);
			return false;
		}
		return false;
	}
	
	private final int ACTION_SELECT_DIALOG=1;
	private final int APP_SELECT_DIALOG=2;
	private final int TRANSPARENT_NOTICE_DIALOG=3;
//	private final int SENSIBILITY_DIALOG=4;
	private final int SHORTCUT_SELECT_DIALOG=5;
	private final int LOWER_WARN_NOTICE=6;
	private final int DETECT_AREA_HEIGHT=7;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case ACTION_SELECT_DIALOG:
				final CharSequence[] items = { 
					getString(R.string.dialog0_none), getString(R.string.dialog0_homebutton),
					getString(R.string.dialog0_recentapp), getString(R.string.dialog0_pullnotification),
					getString(R.string.dialog0_nexttrack), getString(R.string.dialog0_customapp),
					getString(R.string.dialog0_shortcut)
				};
				
				Builder builder0 = new AlertDialog.Builder(this);
				builder0.setTitle(getString(R.string.dialog0_title))
					.setCancelable(true);
				String action=sharedPrefs.getString(currentPref, Util.getDefaultAction(currentPref));
				int index=Integer.parseInt(action.substring(0, 1));
				builder0.setSingleChoiceItems(items, index,
	                    new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int item) {
	//		                    Toast.makeText(getApplicationContext(),items[item], Toast.LENGTH_SHORT).show();
			                	if(item==0||item==1||item==2||item==3|item==4){
			                		Editor editor = sharedPrefs.edit();
				                	editor.putString(currentPref, Integer.toString(item));
				                	editor.commit();
				                	findPreference(currentPref).setSummary(GetActionDescription(Integer.toString(item)));
			                	}
			                    if(item==5){
			                    	showDialog(APP_SELECT_DIALOG);
			                    }
			                    if(item==6){
			                    	showDialog(SHORTCUT_SELECT_DIALOG);
			                    }
			                    dialog.dismiss();  
			                }
			            });
				AlertDialog dialog0 = builder0.create();
				dialog0.show();
				break;
				
				
			case APP_SELECT_DIALOG:
				AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
				builder1.setTitle(getString(R.string.dialog1_title));
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
	                	editor.putString(currentPref,"5|"+pInfo.appName+"|"+pInfo.packageName);
	                	editor.commit();
	                	dialog1.dismiss();
	                	findPreference(currentPref).setSummary(pInfo.appName);
					}
				});
				builder1.setView(appList);
				dialog1 = builder1.create();
				dialog1.show();
				break;
			
		
			case TRANSPARENT_NOTICE_DIALOG:
				AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
		        builder2.setMessage(R.string.dialog_transparenticon_summary)
						.setIcon(android.R.drawable.ic_dialog_info)
		        		.setTitle(R.string.notice_title)
		                .setPositiveButton(R.string.notice_understand, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                       dialog.dismiss();
		                   }
		                })
		                ;
		        // Create the AlertDialog object and return it
		        AlertDialog dialog2=builder2.create();
		        dialog2.show();
				break;
				
//			case SENSIBILITY_DIALOG:
//				AlertDialog.Builder builder_sensibility = new AlertDialog.Builder(this);
//				builder_sensibility.setMessage(R.string.dialog_sensibility_summary)
//						.setIcon(android.R.drawable.ic_dialog_info)
//		        		.setTitle(R.string.notice_title)
//		                .setPositiveButton(R.string.notice_understand, new DialogInterface.OnClickListener() {
//		                   public void onClick(DialogInterface dialog, int id) {
//		                       dialog.dismiss();
//		                   }
//		                })
//		                ;
//		        // Create the AlertDialog object and return it
//		        AlertDialog dialog_sensibility=builder_sensibility.create();
//		        dialog_sensibility.show();
//				break;
				
			case SHORTCUT_SELECT_DIALOG:
				Bundle bundle = new Bundle();
		        ArrayList<String> shortcutNames = new ArrayList<String>();
		        bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);

		        ArrayList<ShortcutIconResource> shortcutIcons = new ArrayList<ShortcutIconResource>();
		        bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);

		        Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
		        pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
		        pickIntent.putExtra(Intent.EXTRA_TITLE, "SELECT SHORTCUT");
		        pickIntent.putExtras(bundle);
		        startActivityForResult(pickIntent, REQUEST_PICK_SHORTCUT);
		        break;
		        
			case LOWER_WARN_NOTICE:
				Builder lowNoticeBuilder = new AlertDialog.Builder(this);
				lowNoticeBuilder
					.setMessage(R.string.dialog_low_warn_summary)
					.setIcon(android.R.drawable.ic_dialog_info)
	        		.setTitle(R.string.notice_title)
					.setCancelable(false)
					.setPositiveButton(getString(R.string.notice_understand),new DialogInterface.OnClickListener() {  
		                @Override  
		                public void onClick(DialogInterface dialog, int which) {  
		                	showDialog(ACTION_SELECT_DIALOG);
		                }
					});
				AlertDialog lowNoticeDialog = lowNoticeBuilder.create();
				lowNoticeDialog.show();
				break;
				
			case DETECT_AREA_HEIGHT:
				View detect_area_height = View.inflate(this, R.layout.detect_area_height, null);
				SeekBar seekbar = (SeekBar) detect_area_height.findViewById(R.id.seekbar);
				final TextView textview=(TextView) detect_area_height.findViewById(R.id.textview);
				final AreaHeight areaHeight=new AreaHeight();
				areaHeight.SetHeight(sharedPrefs.getInt("prefDetectAreaProgress", 2),this);
				seekbar.setProgress(areaHeight.Progress);
				textview.setText(areaHeight.Size);
				seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						areaHeight.SetHeight(progress,SettingsActivity.this);
						textview.setText(areaHeight.Size);						
					}
				});
				Builder detectAreaBuilder = new AlertDialog.Builder(this);
				detectAreaBuilder
					.setMessage(R.string.dialog_areaheight_notice)
					.setIcon(android.R.drawable.ic_dialog_info)
	        		.setTitle(R.string.notice_title)
					.setView(detect_area_height)
					.setPositiveButton(getString(R.string.notice_ok),new DialogInterface.OnClickListener() {  
		                @Override  
		                public void onClick(DialogInterface dialog, int which) {  
		                	Editor editor = sharedPrefs.edit();
		                	editor.putInt("prefDetectAreaProgress",areaHeight.Progress);
		                	editor.commit();
		                }
					});
				AlertDialog dialog = detectAreaBuilder.create();
				dialog.show();
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
		if(action.length()>=1 && action.substring(0, 1).equals("5")){
			String [] splits=action.split("\\|");
			if(splits.length==3){
				return splits[1];
			}
		}
		if(action.length()>=1 && action.substring(0, 1).equals("6")){
			String [] splits=action.split("\\|");
			if(splits.length==3){
				return splits[1];
			}
		}
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
	
	private static final int REQUEST_PICK_SHORTCUT = 1;
	private static final int REQUEST_CREATE_SHORTCUT =2;
	
	
//	private String tempAppName;
	@SuppressWarnings("deprecation")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK){
			switch (requestCode){
				case REQUEST_PICK_SHORTCUT:
//					String tempAppName = data.resolveActivity (getPackageManager()).getShortClassName();
					startActivityForResult(data, REQUEST_CREATE_SHORTCUT);
					break;
					
				case REQUEST_CREATE_SHORTCUT:
					Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
					String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
					String uri=intent.toUri(0);
					Editor editor = sharedPrefs.edit();
					editor.putString(currentPref,"6|"+name+"|"+uri);
					editor.commit();
					findPreference(currentPref).setSummary(name);
			        break;
			}
		}
		
			
	}
}