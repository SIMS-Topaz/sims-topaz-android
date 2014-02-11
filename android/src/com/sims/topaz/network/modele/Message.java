package com.sims.topaz.network.modele;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
	
	private Long id;
	
	@JsonProperty("lat")
	private Double latitude;
	
	@JsonProperty("long")
	private Double longitude;
	
	private String text;
	
	//the number of milliseconds since Jan. 1, 1970, midnight GMT.
	@JsonProperty("date")
	private Long timestamp;
	
	public Message() {
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
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
