package com.sims.topaz;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Toast;


public class MapFragment extends Fragment implements OnMapLongClickListener {
	//I extend FragmentActivity and not Activity in order to have Support MapFragment
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
			setMapIfNeeded();

		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
        return mView;

    }
    
    private void setMapIfNeeded(){
    	mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if(mMap!=null) {
        	mMap.setMyLocationEnabled(true);	
        	mMap.setOnMapLongClickListener(this);
        }
    }
    
	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity().getApplicationContext(), "LOL", Toast.LENGTH_SHORT).show();
		getFragmentManager().beginTransaction().replace(R.id.map, 
				new EditMessageFragment()).commit();
	}
    

}
