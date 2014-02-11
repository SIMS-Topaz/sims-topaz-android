package com.sims.topaz.modele;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.sims.topaz.network.modele.Preview;

public class PreviewClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private Preview preview;
    private String tag;

    public PreviewClusterItem(Preview preview) {
    	this.preview = preview;
        mPosition = new LatLng(preview.getLatitude(), preview.getLongitude());
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
    
    public void setTag(String tag) {
		this.tag = tag;
	}
    
    public String getTag() {
		return tag;
	}
    
    public Preview getPreview() {
		return preview;
	}
}
