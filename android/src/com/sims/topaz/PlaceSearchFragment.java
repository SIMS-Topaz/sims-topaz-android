package com.sims.topaz;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sims.topaz.utils.DebugUtils;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

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

public class PlaceSearchFragment extends Fragment{
	
	private AutoCompleteTextView autoCompView;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		
		Button clearText = (Button) view.findViewById(R.id.auto_clear_text);
		clearText.setVisibility(View.GONE);
		clearText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (autoCompView.getText().length() != 0) {
					autoCompView.setText("");
					((PlacesAutoCompleteAdapter) autoCompView.getAdapter()).clearStoredLocations();
					((Button) getView().findViewById(R.id.auto_clear_text)).setVisibility(View.GONE);
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
				Button bt = (Button) getView().findViewById(R.id.auto_clear_text);
				if (bt.getVisibility() == View.GONE) {
					bt.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					((Button) getView().findViewById(R.id.auto_clear_text)).setVisibility(View.GONE);
				}
			}

		});
	}
	
	
	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	    private ArrayList<String> resultList;
	    private TextView mTextView;

	    private HashMap<String,LatLngBounds> placeLocation = new HashMap<String,LatLngBounds>();
	    
	    public PlacesAutoCompleteAdapter(Context context, int resource, int textViewResourceId) {
	        super(context, resource, textViewResourceId);
	    }
	    
	    public void clearStoredLocations() {
	    	if (!this.placeLocation.isEmpty()) {
	    		this.placeLocation.clear();
	    	}
	    }
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	View view;
	    	if (convertView == null) {
	    		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		view = inflater.inflate(R.layout.adapter_search_bar, null);
	    	} else {
	    		view = convertView;
	    	}
	    	mTextView = (TextView) view.findViewById(R.id.search_bar_text);
	    	mTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
    		if (resultList.get(position) != null) {
    			mTextView.setText(resultList.get(position));
    		}
	    	
	    	view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView textView = (TextView) ((LinearLayout) v).getChildAt(0);
					autoCompView.setText(textView.getText());
					((DrawerActivity) getActivity()).moveCamera(placeLocation.get(textView.getText()));
					clearStoredLocations();
					 InputMethodManager imm = (InputMethodManager)SimsContext.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);
					
				}
				
			});
	    	return view;
	    }
	    

	    @Override
	    public int getCount() {
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
	                    resultList = autocomplete(constraint.toString());
	                    
	                    // Assign the data to the FilterResults
	                    filterResults.values = resultList;
	                    filterResults.count = resultList.size();
	                }
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence constraint, FilterResults results) {
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
	        
	        HttpURLConnection conn = null;
	        StringBuilder jsonResults = new StringBuilder();
	        try {
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
	                	DebugUtils.log("PleaceSearchFragment: does not have JsonObject viewport");
	                }
	            }
	        } catch (JSONException e) {
	        	DebugUtils.log("PleaceSearchFragment: Cannot process JSON results"+ e);
	        }
	        
	        return resultList;
	    }
	}


	
}
