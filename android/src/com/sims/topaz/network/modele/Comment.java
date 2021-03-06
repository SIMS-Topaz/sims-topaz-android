package com.sims.topaz.network.modele;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment  implements Serializable{
	
	private static final long serialVersionUID = 7526472295622776127L;
	private Long id;
	
	@JsonProperty("user_name")
	private String user_name;
	@JsonProperty("user_id")
	private long user_id;
	private String text;
	@JsonProperty("date")
	private Long timestamp;
	
	public Comment(){
		
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setUserName(String owner) {
		this.user_name = owner;
	}

	public Long getId() {
		return id;
	}

	public String getUserName() {
		return user_name;
	}

	public String getText() {
		return text;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public long getUserId() {
		return user_id;
	}

	public void setUserId(long user_id) {
		this.user_id = user_id;
	}

}
