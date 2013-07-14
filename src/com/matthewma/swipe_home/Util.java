package com.matthewma.swipe_home;

import android.content.Context;

public class Util {
	public static final int PrefRatingThreshold=150;
	
	public static String getDefaultAction(String key){
		if(key.equals("prefSwipeup")){
			return "1";
		}
		if(key.equals("prefSwipeupdown")){
			return "2";
		}
		if(key.equals("prefSwipeupleft")){
			return "3";
		}
		if(key.equals("prefSwipeupright")){
			return "3";
		}
		if(key.equals("prefSwipefarup")){
			return "1";
		}
		if(key.equals("prefSwipelowleft")){
			return "2";
		}
		if(key.equals("prefSwipelowright")){
			return "2";
		}
		return "0";
	}
}

class AreaHeight{
	
	public AreaHeight(){
	}
	public int Progress;
	public String Size;
	public int Height;
	public void SetHeight(int progress, Context context){
		Progress=progress;
		switch (progress){
			case 0:
				Height=11;
				Size=context.getString(R.string.pref_areaheight_small);
				break;
			case 1:
				Height=13;
				Size=context.getString(R.string.pref_areaheight_smaller);
				break;
			case 2:
				Height=17;
				Size=context.getString(R.string.pref_areaheight_normal);
				break;
			case 3:
				Height=20;
				Size=context.getString(R.string.pref_areaheight_larger);
				break;
			case 4:
				Height=22;
				Size=context.getString(R.string.pref_areaheight_large);
				break;
		}
	}
}
