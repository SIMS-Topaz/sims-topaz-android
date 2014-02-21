package com.sims.topaz.utils;

import android.util.Log;

public class DebugUtils {
	private static String APP_TAG ="Topaz";
	private static boolean debugMode = true;

	public static void log(String message){
		if(debugMode){
			Log.e(APP_TAG, message);
		}
	}
	
	public static void logException(Exception e){
		if(debugMode){
			Log.e(APP_TAG, e.getLocalizedMessage()+"\n"+e.getMessage());
		}		
	}
}
