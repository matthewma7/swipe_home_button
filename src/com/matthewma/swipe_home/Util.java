package com.matthewma.swipe_home;

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
		return "0";
	}
}
