package com.sims.topaz.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.interfaces.AuthDelegate;
import com.sims.topaz.network.interfaces.SignupDelegate;
import com.sims.topaz.network.modele.ApiResponse;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.network.modele.User;

public class NetworkRestModule {


	//public static final String SERVER_URL = "http://topaz11.apiary.io/api/v1.1/";
	public static final String SERVER_URL = "http://91.121.16.137:8080/api/v1.1/";
	
	private Object delegate;
	
	public NetworkRestModule(Object delegate) {
		this.delegate = (Object) delegate;
	}
	
	/**
	 * Envoi une requete get_message pour recuperer un message
	 * La fin de la requete appellera afterGetMessage() (interface NetworkDelegate)
	 * @param id : l'id du message
	 */
	public void getMessage(Long id) {
		String url = SERVER_URL + "get_message/" + id;
		Log.d("Network getMessage url=", url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.GET_MESSAGE);
		rest.execute();
	}
	
	/**
	 * Envoi une requete get_previews pour recuperer les previews autour d'une zone
	 * La fin de la requete appellera afterGetPreviews() (interface NetworkDelegate)
	 * @param farLeft : coordonnées du bord supérieur gauche
	 * @param nearRight : coordonnéesdu bord inférieur droit
	 */
	public void getPreviews(LatLng farLeft, LatLng nearRight) {
		
		Double minLat = Math.min(farLeft.latitude, nearRight.latitude);
		Double maxLat = Math.max(farLeft.latitude, nearRight.latitude);
		Double minLong = Math.min(farLeft.longitude, nearRight.longitude);
		Double maxLong = Math.max(farLeft.longitude, nearRight.longitude);		
		
		String url = SERVER_URL + "get_previews/" + minLat + "/" + minLong + "/" + maxLat + "/" + maxLong;
		Log.d("Network getPreviews url=", url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.GET_PREVIEW);
		rest.execute();
	}
	
	/**
	 * Poste un message
	 * La fin de la requete appellera afterPostMessage() (interface NetworkDelegate)
	 * @param message le message à poster
	 */
	public void postMessage(Message message) {
		String url = SERVER_URL + "post_message";
		Log.d("Network postMessage url=", url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.POST_MESSAGE);
		ObjectMapper mapper = new ObjectMapper();
		try {
			rest.setJSONParam(mapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		rest.execute();
	}
	
	/**
	 * Inscription d'un utilisateur
	 * La fin de la requete appellera afterSignUp() (interface SignupDelegate)
	 * @param user
	 */
	public void signupUser(User user) {
		String url = SERVER_URL + "signup";
		Log.d("Network signupUser url=", url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.USER_SIGNUP);
		ObjectMapper mapper = new ObjectMapper();
		try {
			rest.setJSONParam(mapper.writeValueAsString(user));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		rest.execute();
	}
	
	private void handleResponse(TypeRequest type, String response) {
		ObjectMapper mapper = new ObjectMapper();

		switch (type) {
			case GET_MESSAGE:
				try {
					ApiResponse<Message> responseData = mapper.readValue(response, new TypeReference<ApiResponse<Message>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((MessageDelegate)delegate).afterGetMessage(responseData.getData());
					}
				} catch (Exception e) {
					((ErreurDelegate)delegate).networkError();
					e.printStackTrace();
				}
				
				break;
			case GET_PREVIEW:
				try {
					ApiResponse<List<Preview>> responseData = mapper.readValue(response, new TypeReference<ApiResponse<List<Preview>>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((MessageDelegate)delegate).afterGetPreviews(responseData.getData());
					}
				} catch (Exception e) {
					((ErreurDelegate) delegate).networkError();
					e.printStackTrace();
				}
				break;	
			case POST_MESSAGE:
				try {
					ApiResponse<Message> responseData = mapper.readValue(response, new TypeReference<ApiResponse<Message>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((MessageDelegate)delegate).afterPostMessage(responseData.getData());
					}
				} catch (Exception e) {
					((ErreurDelegate) delegate).networkError();
					e.printStackTrace();
				}	
				break;
			case COMMENT_MESSAGE:
				// TODO	
				break;
			case USER_SIGNUP:
				try {
					ApiResponse<User> responseData = mapper.readValue(response, new TypeReference<ApiResponse<User>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((SignupDelegate)delegate).afterSignUp(responseData.getData());
					}
				} catch (Exception e) {
					((ErreurDelegate) delegate).networkError();
					e.printStackTrace();
				}	
				break;
			case USER_LOGIN:
				// TODO	
				break;
			default:
				break;
		}
	}

	enum TypeRequest {
		GET_MESSAGE, GET_PREVIEW, POST_MESSAGE, COMMENT_MESSAGE, USER_SIGNUP, USER_LOGIN
	}
	
	
	class RESTTask extends AsyncTask<String, Integer, String> {

		private static final String LOG_TAG = "RESTTask";

		// connection timeout, in milliseconds (waiting to connect)
		private static final int CONN_TIMEOUT = 3000;

		// socket timeout, in milliseconds (waiting for data)
		private static final int SOCKET_TIMEOUT = 5000;

		private Boolean isPost = false;
		
		/*
		 * JSON object for POST
		 */
		private String objectParam = "";

		public void setJSONParam(String object) {
			isPost = true;
			objectParam = object;
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
			// TODO handle pre action ?
		}

		@Override
		protected void onPostExecute(String response) {
			if(module!=null && delegate!=null){
				module.handleResponse(typeRequest, response);
			}
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
					Log.e(LOG_TAG, "doInBackground = " + result);
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
					httppost.setEntity((HttpEntity) new StringEntity(objectParam, "UTF8"));
					httppost.setHeader("Content-type", "application/json");
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
