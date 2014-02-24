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
import com.sims.topaz.R.color;
import com.sims.topaz.utils.SimsContext;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceSearchFragment extends Fragment {
	
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
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_place_search, container, false);
		
		autoCompView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
		final PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(SimsContext.getContext(), 
				R.layout.fragment_place_search, R.id.autoCompleteTextView);
		//adapter.setNotifyOnChange(true);
		autoCompView.setAdapter(adapter);
		
		Button clearText = (Button) view.findViewById(R.id.auto_clear_text);
		clearText.setVisibility(View.GONE);
		clearText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!autoCompView.getText().equals("")) {
					autoCompView.setText("");
					adapter.clearStoredLocations();
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
				// TODO Auto-generated method stub
				Button bt = (Button) getView().findViewById(R.id.auto_clear_text);
				if (bt.getVisibility() == View.GONE) {
					bt.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() == 0) {
					((Button) getView().findViewById(R.id.auto_clear_text)).setVisibility(View.GONE);
				}
			}
		});
	}
	
	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	    private ArrayList<String> resultList;
	    private TextView mTextView;
	    private HashMap<String,LatLng> placeLocation = new HashMap<String,LatLng>();
	    
	    public PlacesAutoCompleteAdapter(Context context, int resource, int textViewResourceId) {
	        super(context, resource, textViewResourceId);
	    }
	    
	    public void clearStoredLocations() {
	    	this.placeLocation.clear();
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
    		if (resultList.get(position) != null) {
    			mTextView.setText(resultList.get(position));
    		}
	    	
	    	view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView textView = (TextView) ((LinearLayout) v).getChildAt(0);
					autoCompView.setText(textView.getText());
					((DrawerActivity) getActivity()).moveCamera(placeLocation.get(textView.getText()));
					//textView.setBackgroundColor(color.black);
					//autoCompView.setDropDownBackgroundResource(color.black);
					clearStoredLocations();
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
	    
	    private static final String LOG_TAG = "Topaz-Android";
	    
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
	            Log.e(LOG_TAG, "Error processing Places API URL", e);
	            return resultList;
	        } catch (IOException e) {
	            Log.e(LOG_TAG, "Error connecting to Places API", e);
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
	                double lat = predsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
	                double lng = predsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
	            	resultList.add(place);
	            	placeLocation.put(place, new LatLng(lat,lng));
	            }
	        } catch (JSONException e) {
	            Log.e(LOG_TAG, "Cannot process JSON results", e);
	        }
	        
	        return resultList;
	    }
	}

	
}
