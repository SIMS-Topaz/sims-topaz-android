package com.sims.topaz.utils;

import android.graphics.Typeface;

public class MyTypefaceSingleton {
    private static Typeface mTypeFace = null;
    private static MyTypefaceSingleton instance;

    private MyTypefaceSingleton() {}

    public static MyTypefaceSingleton getInstance() {
        if (instance == null) {
        	mTypeFace = Typeface.createFromAsset(SimsContext.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            instance = new MyTypefaceSingleton();
        }
        return instance;
    }

    public Typeface getTypeFace() {
        return mTypeFace;
    }
}
