package com.sims.topaz;

import com.sims.topaz.utils.MyTypefaceSingleton;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment  {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_about, container, false);
		TextView textView = (TextView) v.findViewById(R.id.about_text);
		Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
		textView.setTypeface(face);
		return v;
    }
}