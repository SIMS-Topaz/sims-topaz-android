package com.sims.topaz.adapter;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.sims.topaz.R;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.MyTypefaceSingleton;

public class BulleAdapter implements InfoWindowAdapter {

	private LayoutInflater mInflater=null;
	private boolean isCluster;
	private Preview mPreview = null;

	public BulleAdapter(LayoutInflater inflater) {
		this.mInflater=inflater;
		isCluster = false;
	}	    

	public void setIsCluster(boolean is){
		isCluster = is;
	}
	
	@Override
	public View getInfoWindow(Marker marker) {
		return(null);
	}

	@Override
	public View getInfoContents(Marker marker) {
		View popup;
		if(isCluster == false){
			if(mPreview==null) 
				throw new RuntimeException("Preview should not be null");
			
			Log.e("Debug", "isNotCluster");
			popup=mInflater.inflate(R.layout.adapter_info_bulle, null);
			Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
			
			
			// Display the id of the preview
			//TODO : it should be the username
			TextView mBulleTitle=(TextView)popup.findViewById(R.id.bulle_title);
			mBulleTitle.setTypeface(face);
			mBulleTitle.setText(Long.toString(mPreview.getId()));
			
			// Display the text of the preview
			TextView mBulleText=(TextView)popup.findViewById(R.id.bulle_text);
			mBulleText.setTypeface(face);
			mBulleText.setText(mPreview.getText());
			
			// Display the Like/dislike bar
			ProgressBar pg = (ProgressBar) popup.findViewById(R.id.like_bar);
			if(mPreview.getLikes()==0 && mPreview.getDislikes()==0) {
				pg.setIndeterminate(true);
			} else {
				pg.setMax(mPreview.getLikes()+mPreview.getDislikes());
				pg.setProgress(mPreview.getLikes());
				
			}
			
		}else{
			Log.e("Debug", "isCluster");
			popup=mInflater.inflate(R.layout.adapter_info_bulle_cluster, null);
			Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
			
			TextView mBulleMoyen=(TextView)popup.findViewById(R.id.bulle_cluster_moyen_note);
			mBulleMoyen.setTypeface(face);
			TextView mBulleNote=(TextView)popup.findViewById(R.id.bulle_cluster_note);
			mBulleNote.setTypeface(face);
		}

		return(popup);
	}

	public void setPreview(Preview preview) {
		this.mPreview  = preview;
	}
}