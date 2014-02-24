package com.sims.topaz.network.modele;

public class Comment {
	
	private Long id;
	private String owner;
	private String text;
	private Long timestamp;
	
	public Comment(){
		
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public String getOwner() {
		return owner;
	}

	public String getText() {
		return text;
	}

	public Long getTimestamp() {
		return timestamp;
	}

}
