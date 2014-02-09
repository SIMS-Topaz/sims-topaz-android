package com.sims.topaz.utils;

import android.content.Context;

public class SimsContext {
	private static Context context = null;

	public static void setContext(Context context) {
		SimsContext.context = context;
	}
	public static Context getContext() {
		return context;
	}

	public static String getString(int resId){
		return context.getString(resId);
	}
}