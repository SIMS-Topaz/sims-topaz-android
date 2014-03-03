package com.sims.topaz.network.modele;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	
	@JsonProperty("user_id")
	private Long id;
	
	@JsonProperty("user_name")
	private String user_name;
	
	@JsonProperty("user_password")
	private String password;
	
	@JsonProperty("user_email")
	private String email;
	
	@JsonProperty("user_verified")
	private Boolean verified;
	
	@JsonProperty("user_picture")
	private String pictureUrl;

	@JsonProperty("user_comments")
	private Comment comments;	

	@JsonProperty("user_status")
	private String status;		
	
	public User() {
	}

	public Long getId() {
		return id;
	}
	
	public String getUserName() {
		return user_name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public Boolean getVerified() {
		return verified;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setUserName(String name) {
		this.user_name = name;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public Comment getComments() {
		return comments;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toString(){
		return String.valueOf(id)+", "
	+this.user_name + ", " + this.password + ", "+ this.email;
	}
	
}
