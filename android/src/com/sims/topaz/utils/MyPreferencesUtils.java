package com.sims.topaz.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferencesUtils {
	   private static MyPreferencesUtils INSTANCE;
	   private SharedPreferences mPrefs;
	   private SharedPreferences.Editor mEditor;
	   
	   //constants 
	   private static final String SHARED_PREFERENCES = "topaz_shared_preferences";
	   public static final String SHARED_PREFERENCES_CAMERA_MOVEMENT = "topaz_shared_preferences_camera_movement";
	   
	    private MyPreferencesUtils() {}
	    private MyPreferencesUtils(Context c) {
	        mPrefs = c.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
	        mEditor = mPrefs.edit();
	    }

	    public static synchronized MyPreferencesUtils getInstance(Context c) {
	        if (INSTANCE == null) {
	            INSTANCE = new MyPreferencesUtils (c);
	        }
	        return INSTANCE;
	    }

	    public void putBoolean(String key, boolean boolToWrite){
	        mEditor.putBoolean(key, boolToWrite);
	        mEditor.commit();
	    }
	    public boolean getBoolean(String key, boolean defaultValue){
	        return mPrefs.getBoolean(key, defaultValue);
	    }

	    public void putNumber(String key, Integer nb){
	        mEditor.putInt(key,nb);
	        mEditor.commit();
	    }
	    public Integer getNumber(String key, Integer defaultValue ){
	        return mPrefs.getInt(key, defaultValue);
	    }

	    public boolean hasKey(String key){
	        return mPrefs.contains(key);
	    }
}
