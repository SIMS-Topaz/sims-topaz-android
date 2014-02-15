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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterInfoWindowClickListener;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.sims.topaz.adapter.BulleAdapter;
import com.sims.topaz.network.NetworkDelegate;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.LocationUtils;
import com.sims.topaz.utils.SimsContext;
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
OnClusterInfoWindowClickListener<PreviewClusterItem>,
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
	//network call
	private NetworkRestModule mNetworkModule; 
	//bulle
	private BulleAdapter mBulleAdapter; 
	//clusters
	private ClusterManager<PreviewClusterItem> mClusterManager;
	//current values
	private CameraPosition mCurrentCameraPosition;
	private Location mCurrentLocation;
	//constants
	private int mZoomLevel = 12; // the zoom of the map (initially)
	//timers
	private CountDownTimer timerSeconds =  new CountDownTimer(3000, 1000) {   	
		public void onFinish() {
			VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
			mNetworkModule.getPreviews(visibleRegion.farLeft, visibleRegion.nearRight); 
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	};
	private CountDownTimer timerOneMinute =  new CountDownTimer(60000, 1000) {
		public void onFinish() {
			VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
			mNetworkModule.getPreviews(visibleRegion.farLeft, visibleRegion.nearRight); 
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	};
	
	
	/**
	 * This method will only be called once when the retained
	 * Fragment is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Retain this fragment across configuration changes.
		setRetainInstance(true);

	}
	/**
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
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
		return mView;

	}
	/**
	 * Create the Cluster Manager
	 * Set the needed listener to ClusterManager
	 */
	private void setClusterManager(){
		if(mMap!=null){
			mClusterManager = new ClusterManager<PreviewClusterItem>(SimsContext.getContext(), mMap);
			mClusterManager.setRenderer(new PreviewRenderer());
			if(mClusterManager!=null){
				mMap.setOnMarkerClickListener(mClusterManager);
				mMap.setOnInfoWindowClickListener(mClusterManager);
				mClusterManager.onCameraChange(mCurrentCameraPosition); 
				mClusterManager.setOnClusterClickListener(this);
				mClusterManager.setOnClusterItemClickListener(this);
				mClusterManager.setOnClusterItemInfoWindowClickListener(this);
				mClusterManager.setOnClusterInfoWindowClickListener(this);	
			}
		}else{
			Toast.makeText(getActivity(),
					getResources().getString(R.string.map_not_displayed),
					Toast.LENGTH_SHORT)
					.show();
		}
	}
	/**
	 * Set the map and it's listeners
	 * @param inflater
	 */
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
		setClusterManager();
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
		timerOneMinute.cancel();
		timerOneMinute.start();
		mClusterManager.onCameraChange(mCurrentCameraPosition); 
	}

	@Override
	public void onDisconnected() {}

	//Fragment lifecycle----------------------------------------------------------------------------
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
		timerSeconds.start();
		timerOneMinute.start();
	}
	
	/**
	 * The system calls this method as the first indication 
	 * that the user is leaving the fragment 
	 * (though it does not always mean the fragment is being destroyed).
	 */
	@Override
	public void onPause(){
		super.onPause();
		timerSeconds.cancel();
		timerOneMinute.cancel();
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
	//Our Server call listeners----------------------------------------------------------------------------
	@Override
	public void afterPostMessage(Message message) {}

	@Override
	public void afterGetMessage(Message message) {	}
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
	@Override
	public void apiError(ApiError error) {
		Log.e("Debug", "apiError");
	}
	
	//PreviewRenderer----------------------------------------------------------------------------
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

		@SuppressWarnings("rawtypes")
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
		mClusterManager.cluster();
	}
	
	//Clusters listeners----------------------------------------------------------------------------
	@Override
	public boolean onClusterClick(Cluster<PreviewClusterItem> cluster) {
		mBulleAdapter.setIsCluster(true);
		return false;
	}
	@Override
	public void onClusterItemInfoWindowClick(PreviewClusterItem item) {		
		//set fragment
		Bundle args = new Bundle();
		args.putLong("id_preview", item.getPreview().getId());
		CommentFragment fragment = new CommentFragment();
		fragment.setArguments(args);

		//create transaction
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.drawable.animation_slide_in_right,
				R.drawable.animation_slide_out_right);
		transaction.replace(R.id.edit_text, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	@Override
	public boolean onClusterItemClick(PreviewClusterItem item) {
		mBulleAdapter.setIsCluster(false);
		return false;
	}
	@Override
	public void onClusterInfoWindowClick(Cluster<PreviewClusterItem> cluster) {
		Log.e("Debug", "onClusterInfoWindowClick");
	}
	//Location and map listeners----------------------------------------------------------------------------
	@Override
	public void onCameraChange(CameraPosition camera) {
		mCurrentCameraPosition = camera;
		mClusterManager.onCameraChange(camera);
		timerSeconds.cancel();
		timerSeconds.start();
	}
	@Override
	public void onLocationChanged(Location location) {
		if(mCurrentLocation.distanceTo(location)>100){
			VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
			mNetworkModule.getPreviews(visibleRegion.farLeft, visibleRegion.nearRight); 
		}
		mCurrentLocation = location;
	}
	@Override
	public void onMapLoaded() {	
		mCurrentCameraPosition = mMap.getCameraPosition();
		VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
		mNetworkModule.getPreviews(visibleRegion.farLeft, visibleRegion.nearRight); 
	}


	
}
