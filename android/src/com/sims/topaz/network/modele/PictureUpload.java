package com.sims.topaz.network.modele;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PictureUpload {
	
	@JsonProperty("picture_url")
	private String picture_url;
	
	public PictureUpload() {
	}
	
	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
	}
	
	public String getPicture_url() {
		return picture_url;
	}

}
