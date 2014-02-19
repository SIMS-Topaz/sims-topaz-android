package com.sims.topaz;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sims.topaz.utils.SimsContext;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
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
		View view = inflater.inflate(R.layout.fragment_place_search, container, true);
        return view;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		autoCompView = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextView);

		PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(SimsContext.getContext(), 
				R.layout.fragment_place_search, R.id.autoCompleteTextView);
		//adapter.setNotifyOnChange(true);
		autoCompView.setAdapter(adapter);
	    autoCompView.setOnItemClickListener(new OnItemClickListener() {
	    	
	    	@Override
	    	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
	            //String str = (String) adapterView.getItemAtPosition(position);
	            //autoCompView.setText(str);
	    		String str = "ss";
	            Toast.makeText(getActivity().getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	        }
	    	
	    });
	    //setRetainInstance(true);
	}
	
	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	    private ArrayList<String> resultList;
	    
	    public PlacesAutoCompleteAdapter(Context context, int resource, int textViewResourceId) {
	        super(context, resource, textViewResourceId);
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
	    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	    private static final String OUT_JSON = "/json";

	    private static final String API_KEY = "AIzaSyBDo_x_0mwiFuxXZMpLG_m5TRog8K9LsHc";

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
	                resultList.add(predsJsonArray.getJSONObject(i).getString("formatted_address"));
	            }
	        } catch (JSONException e) {
	            Log.e(LOG_TAG, "Cannot process JSON results", e);
	        }
	        
	        return resultList;
	    }
	}

	
}
