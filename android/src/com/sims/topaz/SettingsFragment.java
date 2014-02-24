package com.sims.topaz;

import com.sims.topaz.utils.DebugUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment  {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugUtils.log("SettingsFragment");
		return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}