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
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.sims.topaz.network.interfaces.CommentDelegate;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.LikeStatusDelegate;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.interfaces.PictureUploadDelegate;
import com.sims.topaz.network.interfaces.SignInDelegate;
import com.sims.topaz.network.interfaces.SignUpDelegate;
import com.sims.topaz.network.interfaces.UserDelegate;
import com.sims.topaz.network.modele.ApiResponse;
import com.sims.topaz.network.modele.Comment;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.PictureUpload;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.DebugUtils;

public class NetworkRestModule {

	enum TypeRequest {
		GET_MESSAGE, GET_PREVIEW, POST_MESSAGE, COMMENT_MESSAGE, POST_LIKE_STATUS, USER_SIGNUP, USER_LOGIN, GET_USER_INFO, POST_USER_INFO, PICTURE_UPLOAD
	}

	public static final String SERVER_IMG_BASEURL = "http://91.121.16.137:8080/";
	//public static final String SERVER_URL = "http://topaz13.apiary.io/api/v1.3/";
	public static final String SERVER_URL = "https://91.121.16.137:8081/api/v1.3/";
	//public static final String SERVER_URL = "http://192.168.56.1:8888/";
	
	
	private Object delegate;
	private static HttpClient httpclient;
	private RESTTask lastTask; // TODO à implémenter de partout
	
	public NetworkRestModule(Object delegate) {
		this.delegate = (Object) delegate;
		lastTask = null;
	}
	
	public static void resetHttpClient() {
		httpclient = null;
	}
	
	/**
	 * Envoi une requete get_message pour recuperer un message
	 * La fin de la requete appellera afterGetMessage() (interface NetworkDelegate)
	 * @param id : l'id du message
	 */
	public void getMessage(Long id) {
		String url = SERVER_URL + "get_message/" + id;
		url+="/WITH_COMMENTS";
		DebugUtils.log("Network getMessage url="+ url);
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
		DebugUtils.log("Network getPreviews url="+ url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.GET_PREVIEW);
		rest.execute();
	}
	
	/**
	 * Envoi une requete get_previews pour recuperer les previews autour d'une zone
	 * La fin de la requete appellera afterGetPreviews() (interface NetworkDelegate)
	 * @param farLeft : coordonnées du bord supérieur gauche
	 * @param nearRight : coordonnéesdu bord inférieur droit
	 * @param tag: la string tag pour specifier 
	 */
	public void getPreviewsByTag(LatLng farLeft, LatLng nearRight, CharSequence tag) {
		
		Double minLat = Math.min(farLeft.latitude, nearRight.latitude);
		Double maxLat = Math.max(farLeft.latitude, nearRight.latitude);
		Double minLong = Math.min(farLeft.longitude, nearRight.longitude);
		Double maxLong = Math.max(farLeft.longitude, nearRight.longitude);		
		
		String url = SERVER_URL + "get_previews/" + minLat + "/" + minLong + "/" + maxLat + "/" + maxLong;
		url += "/BY_TAG?tag=" + tag;
		DebugUtils.log("Network getPreviews url="+ url);
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
		DebugUtils.log("Network postMessage url="+ url);
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
	 * Poste une image
	 * La fin de la requete appellera afterUploadPicture() (interface PictureUploadDelegate)
	 * @param pictureData l'image
	 */
	public void uploadPicture(byte[] pictureData) {
		String url = SERVER_URL + "upload_picture";
		DebugUtils.log("Network uploadPicture url="+ url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.PICTURE_UPLOAD);
		rest.setByteData(pictureData);
		rest.execute();
		lastTask = rest;
	}
	
	public void cancelLastTask() {
		if(lastTask != null && !lastTask.isCancelled()) {
			lastTask.cancel(true);
		}
	}
	
	/**
	 * Poste une image
	 * La fin de la requete appellera afterUploadUserPicture() (interface PictureUserUploadDelegate)
	 * @param pictureData l'image
	 */
	public void uploadUserPicture(byte[] pictureData) {
		String url = SERVER_URL + "upload_picture";
		DebugUtils.log("Network uploadPicture url="+ url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.POST_USER_INFO);
		rest.setByteData(pictureData);
		rest.execute();
		lastTask = rest;
	}
	
	
	/**
	 * Poste d'un avis sur un message
	 * @param message message dont on donne l'avis
	 */
	public void postLikeStatus(Message message) {
		String url = SERVER_URL + "post_like_status";
		DebugUtils.log("Network postLikeStatus url="+url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.POST_LIKE_STATUS);
		ObjectMapper mapper = new ObjectMapper();
		Message msgToSend = new Message(message.getId());
		msgToSend.setLikeStatus(message.getLikeStatus());
		try {
			DebugUtils.log(mapper.writeValueAsString(message));
			rest.setJSONParam(mapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		rest.execute();
	}
	
	/**
	 * Poste d'un avis sur un message
	 * @param comment : comment to write
	 * @param message : message which the comment is about
	 */
	public void postComment(Comment comment, Message message) {
		String url = SERVER_URL + "post_comment";
		//add message id in URL
		url+="/"+Long.toString(message.getId());
		DebugUtils.log("Network postComment url="+url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.COMMENT_MESSAGE);
		ObjectMapper mapper = new ObjectMapper();
		try {
			rest.setJSONParam(mapper.writeValueAsString(comment));
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
		DebugUtils.log("Network signupUser url="+ url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.USER_SIGNUP);
		ObjectMapper mapper = new ObjectMapper();
		try {
			rest.setJSONParam(mapper.writeValueAsString(user));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		rest.execute();
	}
	
	/**
	 * Login de l'utilisateur
	 * La fin de la requete appellera afterSignIn() (interface SigninDelegate)
	 * @param user
	 */
	public void signinUser(User user) {
		String url = SERVER_URL + "user_auth";
		DebugUtils.log("Network signuinUser url="+ url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.USER_LOGIN);
		ObjectMapper mapper = new ObjectMapper();
		try {
			
			rest.setJSONParam(mapper.writeValueAsString(user));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		rest.execute();
	}
	
	/**
	 * Envoi une requete get_user pour recuperer les informations d'un user
	 * La fin de la requete appellera afterGetUserInfo() (interface UserDelegate)
	 * @param id : l'id du username
	 */
	public void getUserInfo(Long id) {
		String url = SERVER_URL + "user_info/" + id;
		DebugUtils.log("Network getUserInfo url="+ url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.GET_USER_INFO);
		rest.execute();
	}
	
	/**
	 * Envoi une requete set_user pour setter les informations d'un user
	 * La fin de la requete appellera afterSetUserInfo() (interface UserDelegate)
	 * @param id : l'id du username
	 */
	public void postUserInfo(User user) {
		String url = SERVER_URL + "user_info/" + user.getId();
		DebugUtils.log("Network postUserInfo url="+ url);
		RESTTask rest = new RESTTask(this, url, TypeRequest.POST_USER_INFO);
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
			case POST_LIKE_STATUS:
				try {
					ApiResponse<Message> responseData = mapper.readValue(response, new TypeReference<ApiResponse<Message>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((LikeStatusDelegate)delegate).afterPostLikeStatus(responseData.getData());
					}
				} catch (Exception e) {
					((ErreurDelegate) delegate).networkError();
					e.printStackTrace();
				}
				break;
			case COMMENT_MESSAGE:
				try {
					ApiResponse<Comment> responseData = mapper.readValue(response, new TypeReference<ApiResponse<Comment>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((CommentDelegate)delegate).afterPostComment(responseData.getData());
					}
				} catch (Exception e) {
					((ErreurDelegate) delegate).networkError();
					e.printStackTrace();
				}		
				break;
			case USER_SIGNUP:
				try {
					ApiResponse<User> responseData = mapper.readValue(response, new TypeReference<ApiResponse<User>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((SignUpDelegate)delegate).afterSignUp(responseData.getData());
					}
				} catch (Exception e) {
					((ErreurDelegate) delegate).networkError();
					e.printStackTrace();
				}	
				break;
			case USER_LOGIN:
				try {
					ApiResponse<User> responseData = mapper.readValue(response, new TypeReference<ApiResponse<User>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((SignInDelegate)delegate).afterSignIn(responseData.getData());
					}
				} catch (Exception e) {
					((ErreurDelegate) delegate).networkError();
					e.printStackTrace();
				}	
				break; 
			case GET_USER_INFO:
				try {
					ApiResponse<User> responseData = mapper.readValue(response, new TypeReference<ApiResponse<User>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((UserDelegate)delegate).afterGetUserInfo(responseData.getData());
					}
				} catch (Exception e) {
					((ErreurDelegate) delegate).networkError();
					e.printStackTrace();
				}	
				break; 
			case POST_USER_INFO:
				try {
					ApiResponse<User> responseData = mapper.readValue(response, new TypeReference<ApiResponse<User>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((UserDelegate)delegate).afterPostUserInfo(responseData.getData());
					}
				} catch (Exception e) {
					((ErreurDelegate) delegate).networkError();
					e.printStackTrace();
				}
				break;
			case PICTURE_UPLOAD:
				try {
					ApiResponse<PictureUpload> responseData = mapper.readValue(response, new TypeReference<ApiResponse<PictureUpload>>(){});
					if(responseData.getError() != null) {
						((ErreurDelegate) delegate).apiError(responseData.getError());
					} else {
						((PictureUploadDelegate)delegate).afterUploadPicture(responseData.getData().getPicture_url());
					}
				} catch (Exception e) {
					((ErreurDelegate) delegate).networkError();
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}
	
	class RESTTask extends AsyncTask<String, Integer, String> {

		// connection timeout, in milliseconds (waiting to connect)
		private static final int CONN_TIMEOUT = 3000;

		// socket timeout, in milliseconds (waiting for data)
		private static final int SOCKET_TIMEOUT = 5000;

		private NetworkRestModule module;
		private String url;
		private TypeRequest typeRequest;
		
		private Boolean isPost = false;
		private byte[] byteData = null;
		
		/*
		 * JSON object for POST
		 */
		private String objectParam = "";

		public void setJSONParam(String object) {
			isPost = true;
			objectParam = object;
	    }
		
		public void setByteData(byte[] data) {
			isPost = true;
			byteData = data;
	    }

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
					DebugUtils.log( "doInBackground = " + result);
				} catch (IllegalStateException e) {
					DebugUtils.logException(e);

				} catch (IOException e) {
					DebugUtils.logException(e);
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

			if(httpclient==null)
				httpclient = new TopazHttpClient(getHttpParams());
			HttpResponse response = null;

			try {
				if(isPost) {
					HttpPost httppost = new HttpPost(url);
					
				    if(byteData != null) {
					    MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
					    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	                    ByteArrayBody bab = new ByteArrayBody(byteData, "image");
	                    multipartEntity.addPart("picture", bab);
	                    httppost.setEntity(multipartEntity.build());
				    } else {
						httppost.setEntity((HttpEntity) new StringEntity(objectParam, "UTF8"));
						httppost.setHeader("Content-type", "application/json");
				    }
				    
					response = httpclient.execute(httppost);
					DebugUtils.log( "post = " + objectParam);			    
				    
				} else {
					HttpGet httpget = new HttpGet(url);
					response = httpclient.execute(httpget);
				}
			} catch (Exception e) {
				DebugUtils.logException(e);
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
				DebugUtils.logException(e);
			}

			return total.toString();
		}
	}
}
