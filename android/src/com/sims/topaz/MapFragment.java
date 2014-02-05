package com.sims.topaz;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment{
	private GoogleMap mMap;
    private static View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		//create a new map or use the one that is exists
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null)
				parent.removeView(mView);
		}
        try {
        	
			mView = inflater.inflate(R.layout.fragment_map, container, false);
	        //set map and location 
	        mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	        mMap.setMyLocationEnabled(true);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
        return mView;


    }

}
