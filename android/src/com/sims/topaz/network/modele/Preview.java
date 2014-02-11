package com.sims.topaz.network.modele;

public class Preview {
	
	private Long id;
	private Double latitude;
	private Double longitude;
	private String text;
	private Boolean isFull;
	private Long timestamp;
	
	private final int MAX_CHAR = 10;
	
	public Preview() {
	}
	
	public Preview(Message message) {
		this.id = message.getId();
		this.latitude = message.getLatitude();
		this.longitude = message.getLongitude();
		this.text = message.getText().substring(0,MAX_CHAR);
		this.isFull = text.compareTo(message.getText())==0;
		this.timestamp = message.getTimestamp();
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
