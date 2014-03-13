package com.sims.topaz.network.modele;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable{

	private static final long serialVersionUID = 7526472295622776147L;

	@JsonProperty("user_picture")
	private String pictureUrl;

	@JsonProperty("user_id")
	private Long id;

	@JsonProperty("user_name")
	private String user_name;

	@JsonProperty("user_password")
	private String password;

	@JsonProperty("user_old_password")
	private String old_password;



	@JsonProperty("user_email")
	private String email;

	@JsonProperty("user_verified")
	private Boolean verified;

	@JsonProperty("user_messages")
	private List<Message> messages;	

	@JsonProperty("user_likes")
	private List<Comment> likes;	

	@JsonProperty("user_status")
	private String status;		


	public void setOldPassword(String old_password) {
		this.old_password = old_password;
	}
	
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public User() {
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getStatus() {
		return status;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	@Override
	public String toString(){
		return String.valueOf(id)+", "
				+this.user_name + ", " + this.password + ", "+ this.email;
	}

}
