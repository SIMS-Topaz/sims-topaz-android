package com.sims.topaz.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferencesUtilsSingleton {
	   private static MyPreferencesUtilsSingleton INSTANCE;
	   private SharedPreferences mPrefs;
	   private SharedPreferences.Editor mEditor;
	   
	   //constants 
	   private static final String SHARED_PREFERENCES = "topaz_shared_preferences";
	   public static final String SHARED_PREFERENCES_AUTH_USERNAME = "topaz_shared_preferences_auth_username";
	   public static final String SHARED_PREFERENCES_AUTH_PASSWORD = "topaz_shared_preferences_auth_password";
	   public static final String SHARED_PREFERENCES_VERIFIED = "topaz_shared_preferences_auth_verified";
	   
	    private MyPreferencesUtilsSingleton() {}
	    private MyPreferencesUtilsSingleton(Context c) {
	        mPrefs = c.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
	        mEditor = mPrefs.edit();
	    }

	    public static MyPreferencesUtilsSingleton getInstance(Context c) {
	        if (INSTANCE == null) {
	            INSTANCE = new MyPreferencesUtilsSingleton (c);
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
	    public void putString(String key, String value){
	    	mEditor.putString(key, value);
	    	mEditor.commit();
	    }
	    public String getString(String key, String defValue){
	    	return mPrefs.getString(key, defValue);
	    }
	    public boolean hasKey(String key){
	        return mPrefs.contains(key);
	    }
	    public void removeKey(String key){
	    	mEditor.remove(key);
	    	mEditor.commit();
	    }
}
