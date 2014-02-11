package com.sims.topaz.network.modele;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Preview {
	
	private Long id;
	
	@JsonProperty("lat")
	private Double latitude;
	
	@JsonProperty("long")
	private Double longitude;
	
	private String text;
	
	@JsonProperty("is_full")
	private Boolean isFull;
	
	@JsonProperty("date")
	private Long timestamp;
	
	public Preview() {
	}
	
	public Long getId() {
		return id;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	
	public String getText() {
		return text;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	public Boolean getIsFull() {
		return isFull;
	}
	
	public void setIsFull(Boolean isFull) {
		this.isFull = isFull;
	}
}
