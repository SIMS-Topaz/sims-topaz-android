package com.sims.topaz;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
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
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MapFragment extends Fragment 
implements OnMapLongClickListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
NetworkDelegate, //when called to the server
ClusterManager.OnClusterItemInfoWindowClickListener<PreviewClusterItem>,
ClusterManager.OnClusterClickListener<PreviewClusterItem>,
ClusterManager.OnClusterItemClickListener<PreviewClusterItem>,
OnCameraChangeListener,
LocationListener,
OnMapLoadedCallback 
{
	
	private GoogleMap mMap;
    private static View mView;
    // A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    
    private NetworkRestModule mNetworkModule;
    
    //bulle
    private Marker mMarkerMessage; //stores the marker for wich the user asked for the whole text
    private BulleAdapter mBulleAdapter; 
    
    //constants
    private int mZoomLevel = 12; // the zoom of the map (initially)
    
    private ClusterManager<PreviewClusterItem> mClusterManager;
    
    private CameraPosition mCurrentCameraPosition;
    private Location mCurrentLocation;
 
    private CountDownTimer timerOneSecond =  new CountDownTimer(1000, 1000) {   	
        public void onFinish() {
    		mNetworkModule.getPreviews(mCurrentCameraPosition.target.latitude,
    				mCurrentCameraPosition.target.longitude); 
        }

		@Override
		public void onTick(long millisUntilFinished) {}
     };
     private CountDownTimer timerOneMinute =  new CountDownTimer(60000, 1000) {
         public void onFinish() {
     		mNetworkModule.getPreviews(mCurrentCameraPosition.target.latitude,
    				mCurrentCameraPosition.target.longitude); 
         }
         
		@Override
		public void onTick(long millisUntilFinished) {}
      };       
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
        setClusterManager();
        return mView;

    }
    private void setClusterManager(){
        mClusterManager = new ClusterManager<PreviewClusterItem>(this.getActivity(), mMap);
        mClusterManager.setRenderer(new PreviewRenderer());
        mClusterManager.onCameraChange(mCurrentCameraPosition); 
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);  
        
    }
    private void setMapIfNeeded(LayoutInflater inflater){
    	mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if(mMap!=null) {
        	mMap.setMyLocationEnabled(true);	
        	mMap.setOnMapLongClickListener(this);
        	mBulleAdapter = new BulleAdapter(inflater);
        	mMap.setInfoWindowAdapter(mBulleAdapter);
        	mMap.setOnCameraChangeListener(this);
        	mMap.setOnMapLoadedCallback(this);
        	
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
		EditMessageFragment fragment = new EditMessageFragment();
		fragment.setPosition(point);
		
		transaction.replace(R.id.edit_text, fragment);
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
		mCurrentLocation = mLocationClient.getLastLocation();
		mNetworkModule = new NetworkRestModule(this);
		LocationUtils.onChangeCameraZoom(mCurrentLocation, mZoomLevel, mMap);
		mCurrentCameraPosition = mMap.getCameraPosition();
		timerOneMinute.start();
		mClusterManager.onCameraChange(mCurrentCameraPosition); 
	}

	@Override
	public void onDisconnected() {}
	
    @Override
    public void onStop() {
        // After disconnect() is called, the client is considered "dead".
        if(mLocationClient!=null){
        	mLocationClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onStart() {
        super.onStart();
        if(mLocationClient!=null)
        	mLocationClient.connect();
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
			
		}
	}
	@Override
	public void afterGetPreviews(List<Preview> list) {
		mClusterManager.clearItems();
		List<PreviewClusterItem> items = new ArrayList<PreviewClusterItem>();
		for(Preview p:list){
			String text = p.getText();
			String tag = text.substring(text.lastIndexOf("#")+1);
			PreviewClusterItem item = new PreviewClusterItem(p);
			item.setTag(tag);
			items.add(item);
		}	
		mClusterManager.addItems(items);
		mClusterManager.onCameraChange(mCurrentCameraPosition);
		mClusterManager.cluster();
	}
	
    private class PreviewRenderer extends DefaultClusterRenderer<PreviewClusterItem> {

        public PreviewRenderer() {
            super(mView.getContext(), mMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(PreviewClusterItem item, MarkerOptions markerOptions) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(TagUtils.getDrawableForString(item.getTag()));
            markerOptions.icon(icon)
            .title(item.getPreview().getTimestamp().toString())
            .snippet(item.getPreview().getText());
            
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }


	public void onNewMessage(Message message) {
		Preview preview = new Preview(message);
		PreviewClusterItem pci = new PreviewClusterItem(preview);
		mClusterManager.addItem(pci);
		CameraPosition cp = mMap.getCameraPosition();
		mClusterManager.onCameraChange(cp);
	}
    

	@Override
	public boolean onClusterClick(Cluster<PreviewClusterItem> cluster) {
		mBulleAdapter.setIsCluster(true);
		return false;
	}
	@Override
	public void onClusterItemInfoWindowClick(PreviewClusterItem item) {
		mNetworkModule.getMessage(item.getPreview().getId());
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.drawable.animation_bottom_up,
				R.drawable.animation_bottom_down);
		transaction.replace(R.id.edit_text, new CommentFragment());
		transaction.addToBackStack(null);
		transaction.commit();	
	}
	@Override
	public boolean onClusterItemClick(PreviewClusterItem item) {
		mBulleAdapter.setIsCluster(false);
		return false;
	}
	@Override
	public void onCameraChange(CameraPosition camera) {
		mCurrentCameraPosition = camera;
		mClusterManager.onCameraChange(camera);	
		timerOneSecond.start();
	}
	@Override
	public void onLocationChanged(Location location) {
		if(mCurrentLocation.distanceTo(location)>100){
    		mNetworkModule.getPreviews(mCurrentCameraPosition.target.latitude,
    				mCurrentCameraPosition.target.longitude); 
		}
		mCurrentLocation = location;
		
		
	}
	@Override
	public void onMapLoaded() {	
		mCurrentCameraPosition = mMap.getCameraPosition();
		mNetworkModule.getPreviews(mCurrentCameraPosition.target.latitude,
				mCurrentCameraPosition.target.longitude); 
	}

}
