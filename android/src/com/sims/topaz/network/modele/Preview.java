package com.sims.topaz.network.modele;

public class Preview {
	
	private Long id;
	private Double latitude;
	private Double longitude;
	private String text;
	private Boolean isFull;
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
