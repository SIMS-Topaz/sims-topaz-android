package com.sims.topaz.AsyncTask;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.sims.topaz.utils.DebugUtils;

public class LoadPictureTask extends AsyncTask<String, Void, Drawable> {
	
	protected WeakReference<Object> delegate;
	
	public LoadPictureTask(Object mDelegate) {
		this.delegate = new WeakReference<Object>(mDelegate);
	}
	
	public interface LoadPictureTaskInterface{
		public void loadPictureTaskOnPostExecute(Drawable image);	
	}
	
	@Override
	protected Drawable doInBackground(String... urls) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        try {
			InputStream is = (InputStream) new URL(urls[0]).getContent();
			return Drawable.createFromStream(is, "picture");

		} catch (Exception e) {
			DebugUtils.logException(e);
			Log.e("TAG", Log.getStackTraceString(e));
		}
		return null;
     }
	
	@Override
	protected void onPostExecute(Drawable image) {
		try{
			((LoadPictureTaskInterface)this.delegate.get()).loadPictureTaskOnPostExecute(image);
		}catch(Exception e){}
	}
 }