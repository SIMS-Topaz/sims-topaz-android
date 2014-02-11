package com.sims.topaz.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.sims.topaz.R;
import com.sims.topaz.utils.MyTypefaceSingleton;

public class BulleAdapter implements InfoWindowAdapter {

	private LayoutInflater mInflater=null;
	private String mAllText;
	private boolean isCluster;

	public BulleAdapter(LayoutInflater inflater) {
		this.mInflater=inflater;
		isCluster = false;
	}	    
	public BulleAdapter(LayoutInflater inflater, String mAllText) {
		this.mInflater=inflater;
		this.mAllText= mAllText;
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
			popup=mInflater.inflate(R.layout.adapter_info_bulle, null);
			Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
			
			TextView mBulleTitle=(TextView)popup.findViewById(R.id.bulle_title);
			mBulleTitle.setTypeface(face);
			mBulleTitle.setText(marker.getTitle());
			
			TextView mBulleText=(TextView)popup.findViewById(R.id.bulle_text);
			mBulleText.setTypeface(face);
			if(mAllText!=null && !mAllText.equals("")){
				mBulleText.setText(mAllText);
			}else{
				mBulleText.setText(marker.getSnippet());
			}
		}else{
			popup=mInflater.inflate(R.layout.adapter_info_bulle_cluster, null);
			Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
			
			TextView mBulleMoyen=(TextView)popup.findViewById(R.id.bulle_cluster_moyen_note);
			mBulleMoyen.setTypeface(face);
			TextView mBulleNote=(TextView)popup.findViewById(R.id.bulle_cluster_note);
			mBulleNote.setTypeface(face);
		}

		return(popup);
	}
}