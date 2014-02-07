package com.sims.topaz;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.sims.topaz.network.NetworkDelegate;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.LocationUtils;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MapFragment extends Fragment 
implements OnMapLongClickListener,
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
OnCameraChangeListener,
NetworkDelegate{
	
	private GoogleMap mMap;
    private static View mView;
    // A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    
    private List<Marker> mDisplayedMarkers;
    private NetworkRestModule mNetworkModule;
    
    
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
        
        mDisplayedMarkers = new ArrayList<Marker>();
        mNetworkModule = new NetworkRestModule(this);
        return mView;

    }
    
    private void setMapIfNeeded(){
    	mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if(mMap!=null) {
        	mMap.setMyLocationEnabled(true);	
        	mMap.setOnMapLongClickListener(this);
        	mMap.setOnCameraChangeListener(this);
        }
        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();
        //Set the update interval
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        //Create a new location client, using the enclosing class to handle callbacks.
        mLocationClient = new LocationClient(getActivity().getApplicationContext(),
        		this,
        		this);
    }
    
	@Override
	public void onMapLongClick(LatLng point) {
		getFragmentManager().beginTransaction().replace(R.id.map, 
				new EditMessageFragment()).commit();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(getActivity()
				, getResources().getString(R.string.connection_error_code)
				,Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		Location location = mLocationClient.getLastLocation();
		// TODO si location null ?
		if(location != null) {
		    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
		    mMap.animateCamera(cameraUpdate);
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		mNetworkModule.getPreviews(location.getLatitude(),location.getLongitude());

	}
	
    @Override
    public void onStop() {

        // If the client is connected
        if (mLocationClient!=null && mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }
        // After disconnect() is called, the client is considered "dead".
        if(mLocationClient!=null){
        	mLocationClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mLocationClient!=null)
        	mLocationClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        switch (requestCode) {
            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :
                Log.d(Integer.toString(LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST),
                        getString(R.string.unknown_activity_request_code, requestCode));            	
            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(LocationUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));
               break;
        }
    }
    
    private void startPeriodicUpdates() {
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
    }

	@Override
	public void onCameraChange(CameraPosition camera) {
		mNetworkModule.getPreviews(camera.target.latitude,
										camera.target.longitude);
		
	}



	@Override
	public void networkError() {
		Toast.makeText(getActivity(),
				getResources().getString(R.string.network_error),
				Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void afterPostMessage(Message message) {
	}

	@Override
	public void afterGetMessage(Message message) {

		Marker m = mMap.addMarker(new MarkerOptions()
        .position(new LatLng(message.getLatitude(),
      		  message.getLongitude()))
        .rotation((float) 90.0)
        .anchor((float) 0.5, (float) 0.5)
        .title(String.valueOf(message.getTimestamp()))
        .snippet(message.getText()));
		mDisplayedMarkers.add(m);
		
	}

	@Override
	public void afterGetPreviews(List<Preview> list) {
		for(Preview p:list){
			Marker m = mMap.addMarker(new MarkerOptions()
								        .position(new LatLng(p.getLatitude(),
								      		  p.getLongitude()))
								        .anchor((float) 0.5, (float) 0.5)
								        .title(String.valueOf(p.getTimestamp()))
								        .snippet(p.getText()));
			mDisplayedMarkers.add(m);
		}	
		
	}
    

}
