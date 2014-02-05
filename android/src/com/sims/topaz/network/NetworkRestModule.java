package com.sims.topaz.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
public class NetworkRestModule {
	
	public static final String SERVER_URL = "http://topaz1.apiary.io/";
	
	private NetworkDelegate delegate;
	
	public NetworkRestModule(NetworkDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void initGetMessage(Long id) {
		RESTTask rest = new RESTTask(this, SERVER_URL + "get_message" + id, TypeRequest.GET_MESSAGE);
		rest.execute();
	}
	
	public void initGetPreviews(Double latitude, Double longitude) {
		RESTTask rest = new RESTTask(this, SERVER_URL + "get_previews", TypeRequest.GET_PREVIEW);
		rest.addParam("latitude", latitude.toString());
		rest.addParam("longitude", longitude.toString());
		rest.execute();
	}
	
	public void handleResponse(TypeRequest type, String response) {
		ObjectMapper mapper = new ObjectMapper();
		switch (type) {
			case GET_MESSAGE:
				try {
					Message message = mapper.readValue(response, Message.class);
					delegate.displayMessage(message);
				} catch (Exception e) {
					delegate.networkError();
					e.printStackTrace();
				}
				
				break;
			case GET_PREVIEW:
				try {
					List<Preview> list = mapper.readValue(response, new TypeReference<List<Preview>>(){});
					delegate.displayPreviews(list);
				} catch (Exception e) {
					delegate.networkError();
					e.printStackTrace();
				}
				break;	
			case POST_MESSAGE:
				
				break;
			default:
				break;
		}
	}

	enum TypeRequest {
		GET_MESSAGE, GET_PREVIEW, POST_MESSAGE
	}
	
	
	
	class RESTTask extends AsyncTask<String, Integer, String> {

		private static final String LOG_TAG = "RESTTask";

		// connection timeout, in milliseconds (waiting to connect)
		private static final int CONN_TIMEOUT = 3000;

		// socket timeout, in milliseconds (waiting for data)
		private static final int SOCKET_TIMEOUT = 5000;

		private Boolean isPost = false;

		private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		public void addParam(String name, String value) {
			isPost = true;
			params.add(new BasicNameValuePair(name, value));
	    }
		
		private NetworkRestModule module;
		private String url;
		private TypeRequest typeRequest;
		
		
		public RESTTask(NetworkRestModule module, String url, TypeRequest typeRequest) {
			this.module = module;
			this.url = url;
			this.typeRequest = typeRequest;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO init progress bar ...
		}

		@Override
		protected void onPostExecute(String response) {
			Log.d(LOG_TAG, "onPostExecute response=" + response);
			module.handleResponse(typeRequest, response);
		}

		@Override
		protected String doInBackground(String... args) {

			String result = "";
			HttpResponse response = doResponse(url);
			
			if (response == null) {
				return result;
			} else {
				try {
					result = inputStreamToString(response.getEntity().getContent());
				} catch (IllegalStateException e) {
					Log.e(LOG_TAG, e.getLocalizedMessage(), e);

				} catch (IOException e) {
					Log.e(LOG_TAG, e.getLocalizedMessage(), e);
				}
			}

			return result;
		}

		// Establish connection and socket (data retrieval) timeouts
		private HttpParams getHttpParams() {
			HttpParams htpp = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
			HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);
			return htpp;
		}

		private HttpResponse doResponse(String url) {

			HttpClient httpclient = new DefaultHttpClient(getHttpParams());
			HttpResponse response = null;

			try {
				if(isPost) {
					HttpPost httppost = new HttpPost(url);
					httppost.setEntity(new UrlEncodedFormEntity(params));
					response = httpclient.execute(httppost);
				} else {
					HttpGet httpget = new HttpGet(url);
					response = httpclient.execute(httpget);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getLocalizedMessage(), e);
			}

			return response;
		}

		private String inputStreamToString(InputStream is) {

			String line = "";
			StringBuilder total = new StringBuilder();

			// Wrap a BufferedReader around the InputStream
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				// Read response until the end
				while ((line = rd.readLine()) != null) {
					total.append(line);
				}
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getLocalizedMessage(), e);
			}

			return total.toString();
		}
	}
}
