package com.sims.topaz.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthUtils {
	
	public static boolean isValidUsername(String username) {
	    String expression = "^[a-z0-9_]+$";
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
	
	public final static String getSessionValue(String key) {
		return MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).getString(key, "");
	}
	
	public final static void setSession(String username, String userpassword) {
		MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).putString(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME, username);
		MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).putString(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_PASSWORD, userpassword);
	}
	
	public final static void unsetSession() {
		if(MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).hasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME)) {
			MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).removeKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME);
		}
		if(MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).hasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_PASSWORD)) {
			MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext()).removeKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_PASSWORD);
		}
	}

}
