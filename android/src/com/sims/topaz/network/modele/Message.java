package com.sims.topaz.network.modele;

import java.util.ArrayList;
import java.util.List;
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
	
	private String owner;
	
	private int likes=0;
	
	private int dislikes=0;
	
	//the number of milliseconds since Jan. 1, 1970, midnight GMT.
	@JsonProperty("date")
	private Long timestamp;
	
	private List<Comment> comments = null;
	
	public enum eLikeStatus {
		NONE,
		LIKED,
		DISLIKED;
	}
	public eLikeStatus likeStatus = eLikeStatus.NONE;
	
	public Message() {
		comments = new ArrayList<Comment>();
	}
	
	public Message(Long id) {
		this.id = id;
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

	public void setLikeStatus(eLikeStatus likeStatus) {
		this.likeStatus = likeStatus;
	}

	public Long getId() {
		return id;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	
	public eLikeStatus getLikeStatus() {
		return likeStatus;
	}

	public String getText() {
		return text;
	}
	
	public Long getTimestamp() {
		return timestamp;
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

	public void like() {
		if(likeStatus!=eLikeStatus.LIKED) {
			likes++;
			likeStatus=eLikeStatus.LIKED;
		}
	}
	public void unlike() {
		if(likeStatus==eLikeStatus.LIKED) {
			likes--;
			likeStatus=eLikeStatus.NONE;
		}
	}
	public void dislike() {
		if(likeStatus!=eLikeStatus.DISLIKED) {
			dislikes++;
			likeStatus=eLikeStatus.DISLIKED;
		}
	}
	public void undislike() {
		if(likeStatus==eLikeStatus.DISLIKED) {
			dislikes--;
			likeStatus=eLikeStatus.NONE;
		}
	}

	public List<Comment> getComments() {
		return comments;
	}
	
}
