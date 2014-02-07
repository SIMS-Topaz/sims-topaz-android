package com.sims.topaz;

import com.sims.topaz.utils.MyPreferencesUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

public class SettingsFragment extends Fragment  {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    
    public void onToggleCameraMovementClicked(View view){
    	boolean isOnOrOff = ((ToggleButton) view).isChecked();
		MyPreferencesUtils.getInstance(getActivity())
		.putBoolean(MyPreferencesUtils.SHARED_PREFERENCES_CAMERA_MOVEMENT,isOnOrOff);   	
    }
}
