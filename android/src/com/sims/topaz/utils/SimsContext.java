package com.sims.topaz.utils;

import android.content.Context;
/**
 * Used in order to optimize the use of the context 
 * by having it saved only once
 */
public class SimsContext {
	private static Context context = null;

	public static void setContext(Context context) {
		SimsContext.context = context;
		TagUtils.setTagUtils();
	}
	public static Context getContext() {
		return context;
	}

	public static String getString(int resId){
		return context.getString(resId);
	}
}
