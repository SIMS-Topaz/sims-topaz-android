package com.sims.topaz.network.modele;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	
	@JsonProperty("user_id")
	private Long id;
	
	@JsonProperty("user_name")
	private String name;
	
	@JsonProperty("user_password")
	private String password;
	
	@JsonProperty("user_email")
	private String email;
	
	@JsonProperty("user_verified")
	private Boolean verified;
	
	public User() {
	}

	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
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
	
	public void setName(String name) {
		this.name = name;
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
	
	@Override
	public String toString(){
		return String.valueOf(id)+", "
	+this.name + ", " + this.password + ", "+ this.email;
	}
	
}
