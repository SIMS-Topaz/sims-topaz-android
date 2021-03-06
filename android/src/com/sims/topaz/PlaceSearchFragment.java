package com.sims.topaz;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.api.GoogleApiClient.ApiOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.VisibleRegion;
import com.sims.topaz.interfaces.OnMoveCamera;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.DebugUtils;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;
import com.sims.topaz.utils.TagUtils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceSearchFragment extends Fragment  {

	private AutoCompleteTextView autoCompView;
	private Button clearText;
	OnMoveCamera mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnMoveCamera) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnMoveCamera");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_place_search, container, false);
		autoCompView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
		autoCompView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(SimsContext.getContext(), 
				R.layout.fragment_place_search, R.id.autoCompleteTextView);
		autoCompView.setAdapter(adapter);

		clearText = (Button) view.findViewById(R.id.auto_clear_text);
		clearText.setVisibility(View.GONE);
		clearText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (autoCompView.getText().length() != 0) {
					autoCompView.setText("");
					//((PlacesAutoCompleteAdapter) autoCompView.getAdapter()).clearStoredLocations();
					clearText.setVisibility(View.GONE);
				}
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		autoCompView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (clearText.getVisibility() == View.GONE) {
					clearText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				View v = getView().findViewById(autoCompView.getDropDownAnchor());
				if (v != null ) {
					v.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					clearText.setVisibility(View.GONE);
				}
				View v = getView().findViewById(autoCompView.getDropDownAnchor());
				if (v != null ) {
					v.setVisibility(View.VISIBLE);
				}
			}

		});
	}



	//Adapter
	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
		private ArrayList<String> resultList;




		private HashMap<String,LatLngBounds> placeLocation = new HashMap<String,LatLngBounds>();

		public PlacesAutoCompleteAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		public void clearStoredLocations() {
			if (!this.placeLocation.isEmpty()) {
				this.placeLocation.clear();
			}
		}

		private class ViewHolder {
			TextView mTextView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.adapter_search_bar, null);
				holder.mTextView = (TextView) view.findViewById(R.id.search_bar_text);
				holder.mTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			if (resultList.get(position) != null) {
				holder.mTextView.setText(resultList.get(position));
			}

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					TextView textView = (TextView) ((LinearLayout) v).getChildAt(0);
					String input = textView.getText().toString();
					autoCompView.setText(input);

					if(!placeLocation.isEmpty()){
						mCallback.moveCamera(placeLocation.get(textView.getText()));

					}else{
						//Execute search 
						onExecuteSearch(input);				
					}
					InputMethodManager imm = (InputMethodManager)SimsContext.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);					
					//clearStoredLocations();
					View dropdown = getActivity().findViewById(autoCompView.getDropDownAnchor());
					if (dropdown != null) {
						dropdown.setVisibility(View.GONE);
					}
				}

			});
			return view;
		}

		private void onExecuteSearch(String input){
			List<String> listTags = new ArrayList<String>();
			listTags.add(input);
			((DrawerActivity)mCallback).onSetTagFilter(listTags);
		}

		@Override
		public int getCount() {
			if(resultList==null) return 0;
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the auto-completed results.
						ArrayList<String> results = autocomplete(constraint.toString());
						filterResults.values = results;
						// Assign the data to the FilterResults
						filterResults.count = results.size();
					}
					return filterResults;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					try {
						resultList = (ArrayList<String>) results.values;
					} catch (ClassCastException e){
						resultList = null;
					}

					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					}
					else {
						notifyDataSetInvalidated();
					}
				}};
				return filter;
		}

		private static final String PLACES_API_BASE = "https://maps.google.com/maps/api/geocode";
		private static final String OUT_JSON = "/json";


		private ArrayList<String> autocomplete(String input) {
			ArrayList<String> resultList = null;
			boolean isTag = false;
			input = input.trim();

			HttpURLConnection conn = null;
			StringBuilder jsonResults = new StringBuilder();
			try {
				if(!input.startsWith("#")){
					//Search for location
					StringBuilder sb = new StringBuilder(PLACES_API_BASE + OUT_JSON);
					sb.append("?sensor=false");
					sb.append("&address=" + URLEncoder.encode(input, "utf8"));

					URL url = new URL(sb.toString());
					conn = (HttpURLConnection) url.openConnection();
					InputStreamReader in = new InputStreamReader(conn.getInputStream());

					// Load the results into a StringBuilder
					int read;
					char[] buff = new char[1024];
					while ((read = in.read(buff)) != -1) {
						jsonResults.append(buff, 0, read);
					}
				}else{
					//Search for tag
					isTag = true;
					List<String> tagList = TagUtils.getAllTags();
					resultList = new ArrayList<String>();
					placeLocation.clear();
					for(String element:tagList){
						if(element.contains(input)){	        			
							resultList.add(element);
						}
					}
					return resultList;

				}
			} catch (MalformedURLException e) {
				DebugUtils.log("PleaceSearchFragment: Error processing Places API URL"+ e);
				return resultList;
			} catch (IOException e) {
				DebugUtils.log("PleaceSearchFragment: Error connecting to Places API"+ e);
				return resultList;
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

			try {
				if(isTag == false){
					// Create a JSON object hierarchy from the results
					JSONObject jsonObj = new JSONObject(jsonResults.toString());
					JSONArray predsJsonArray = jsonObj.getJSONArray("results");

					// Extract the Place descriptions from the results
					resultList = new ArrayList<String>(predsJsonArray.length());
					for (int i = 0; i < predsJsonArray.length(); i++) {
						String place = predsJsonArray.getJSONObject(i).getString("formatted_address");
						JSONObject ancester = predsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("viewport");
						if (ancester != null) {
							LatLng southwest = new LatLng(ancester.getJSONObject("southwest").getDouble("lat"), 
									ancester.getJSONObject("southwest").getDouble("lng"));
							LatLng northeast = new LatLng(ancester.getJSONObject("northeast").getDouble("lat"), 
									ancester.getJSONObject("northeast").getDouble("lng"));
							resultList.add(place);
							placeLocation.put(place, new LatLngBounds(southwest,northeast));
						} else {
							DebugUtils.log("PlaceSearchFragment: does not have JsonObject viewport");
						}
					}
				}
			} catch (JSONException e) {
				DebugUtils.log("PlaceSearchFragment: Cannot process JSON results" + e);
			}

			return resultList;
		}
	}


}
