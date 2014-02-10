package com.sims.topaz;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.sims.topaz.adapter.BulleAdapter;
import com.sims.topaz.network.NetworkDelegate;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.LocationUtils;
import com.sims.topaz.utils.TagUtils;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


public class MapFragment extends Fragment 
implements OnMapLongClickListener,
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
OnCameraChangeListener,
NetworkDelegate,//when called to the server
OnInfoWindowClickListener //when clicked on the marker
{
	
	private GoogleMap mMap;
    private static View mView;
    // A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    
    private Map<Marker, Preview> mDisplayedMarkers;
    private NetworkRestModule mNetworkModule;
    
    //bulle
    private Marker mMarkerMessage; //stores the marker for wich the user asked for the whole text
    private BulleAdapter mBulleAdapter; 
    
    //constants
    private int mZoomLevel = 12; // the zoom of the map (initially)
    
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
			setMapIfNeeded(inflater);

		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
        
        mDisplayedMarkers = new HashMap<Marker, Preview>();;
        mNetworkModule = new NetworkRestModule(this);
        return mView;

    }
    
    private void setMapIfNeeded(LayoutInflater inflater){
    	mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if(mMap!=null) {
        	mMap.setMyLocationEnabled(true);	
        	mMap.setOnMapLongClickListener(this);
        	mMap.setOnCameraChangeListener(this);
        	mBulleAdapter = new BulleAdapter(inflater);
        	mMap.setInfoWindowAdapter(mBulleAdapter);
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
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.drawable.animation_bottom_up,
				R.drawable.animation_bottom_down);
		transaction.replace(R.id.edit_text, new EditMessageFragment());
		transaction.addToBackStack(null);
		transaction.commit();
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
		LocationUtils.onChangeCameraZoom(location, mZoomLevel, mMap);
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
		//If I have to change the bulle to mMarkerMessage
		if(mMarkerMessage.getPosition().latitude == message.getLatitude() 
				&& mMarkerMessage.getPosition().longitude == message.getLongitude()){
			mBulleAdapter.setAllText(message.getText());
		}
	}

	@Override
	public void afterGetPreviews(List<Preview> list) {		
			for(Preview p:list){
				String text = p.getText();
				String tag = text.substring(text.lastIndexOf("#")+1);
				Marker m = mMap.addMarker(new MarkerOptions()
									        .position(new LatLng(p.getLatitude(),
									      		  p.getLongitude()))
									        .anchor((float) 0.5, (float) 0.5)
									        .title(String.valueOf(p.getTimestamp()))
									        .snippet(p.getText())
									        .icon(BitmapDescriptorFactory.fromResource(TagUtils.getDrawableForString(tag))));
				
				mDisplayedMarkers.put(m, p);
			}	
			
	}
	@Override
	public void onInfoWindowClick(Marker marker) {
		Preview p = mDisplayedMarkers.get(marker);
		//get All Message
		mMarkerMessage = marker;
		mNetworkModule.getMessage(p.getId());
	}
    
}
