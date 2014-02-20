package com.sims.topaz.network.modele;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Preview {
	
	private Long id;
	
	@JsonProperty("lat")
	private Double latitude;
	
	@JsonProperty("long")
	private Double longitude;
	
	private String text;
	
	private String owner;
	
	@JsonProperty("is_full")
	private Boolean isFull;
	
	@JsonProperty("date")
	private Long timestamp;
	
	private final int MAX_CHAR = 20;
	
	private int likes = 0;
	
	private int dislikes = 0;
	
	public Preview() {
	}
	
	public Preview(Message message) {
		this.id = message.getId();
		this.latitude = message.getLatitude();
		this.longitude = message.getLongitude();
		if(message.getText().length() > MAX_CHAR) {
			this.text = message.getText().substring(0,MAX_CHAR);
			this.isFull = false;
		} else {
			this.text = message.getText();
			this.isFull = true;
		}
		this.timestamp = message.getTimestamp();
		this.likes = message.getLikes();
		this.dislikes = message.getDislikes();
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

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getDislikes() {
		return dislikes;
	}

	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
	}

	public String getOwner() {
		return owner;
	}
}
