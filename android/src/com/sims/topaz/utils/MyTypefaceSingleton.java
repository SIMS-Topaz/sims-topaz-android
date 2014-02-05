package com.sims.topaz.utils;

import android.graphics.Typeface;

public class MyTypefaceSingleton {
    private static Typeface mTypeFace = null;
    private static MyTypefaceSingleton instance = null;

    private MyTypefaceSingleton() {
        mTypeFace = Typeface.createFromAsset(SimsContext.getContext().getAssets(), "fonts/Roboto-Light.ttf");
    }

    public static synchronized MyTypefaceSingleton getInstance() {
        if (instance == null) {
            instance = new MyTypefaceSingleton();
        }
        return instance;
    }

    public static Typeface getTypeFace() {
        return mTypeFace;
    }
}
