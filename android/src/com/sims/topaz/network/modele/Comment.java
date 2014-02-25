package com.sims.topaz.network.modele;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Comment {
	
	private Long id;
	
	@JsonProperty("user_name")
	private String user_name;
	private String text;
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

}
