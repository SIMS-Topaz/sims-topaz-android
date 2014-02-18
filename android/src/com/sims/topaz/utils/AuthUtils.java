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

}
