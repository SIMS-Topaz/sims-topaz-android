package com.sims.topaz.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthUtils {
	
	public static boolean isValidUsername(String username) {
	    String expression = "^[a-zA-Z0-9_]+$";
	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(username);
	    return matcher.matches();
	}
	
	public static boolean isValidPassword(String password, Integer minLen) {
	    return password != null && password.length() >= minLen;
	}
	
	public static boolean isValidPassword(String password, String confirm, Integer minLen) {
	    return password != null && password.equals(confirm) && password.length() >= minLen;
	}
	
	public final static boolean isValidEmail(String mail) {
	    if (mail == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
	    }
	}

	public final static Boolean sessionHasKey(String key) {
		return MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).hasKey(key);
	}
	
	public final static String getSessionStringValue(String key) {
		return MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).getString(key, "");
	}
	
	public final static boolean getSessionBoolValue(String key, Boolean defaultValue) {
		return MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).getBoolean(key, defaultValue);
	}
	public final static long getSessionLongValue(String key, long defaultValue) {
		return MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).getLong(key, defaultValue);
	}	
	public final static void setSession(String username, String userpassword, Boolean verified , long id) {
		MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).putString(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME, username);
		MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).putString(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_PASSWORD, userpassword);
		MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).putLong(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_ID, id);
		if(verified != null) {
			MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).putBoolean(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_VERIFIED, verified);
		}
	}
	
	public final static void unsetSession() {
		if(MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).hasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME)) {
			MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).removeKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME);
		}
		if(MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).hasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_PASSWORD)) {
			MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).removeKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_PASSWORD);
		}
		if(MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).hasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_VERIFIED)) {
			MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).removeKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_VERIFIED);
		}
		if(MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).hasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_ID)) {
			MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).removeKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_ID);
		}
	}

}
