package com.sims.topaz.network.modele;

public class Message {
	
	private Long id;
	private Double latitude;
	private Double longitude;
	private String text;
	private Long timestamp;
	
	public Message() {
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
	
}
