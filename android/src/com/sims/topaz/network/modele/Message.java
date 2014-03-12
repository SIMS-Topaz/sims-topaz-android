package com.sims.topaz.network.modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message  implements Serializable {
	private static final long serialVersionUID = 7526472295622776177L;

	private Long id;
	
	@JsonProperty("lat")
	private Double latitude;
	
	@JsonProperty("long")
	private Double longitude;
	
	private String text;
	
	@JsonProperty("picture_url")
	private String pictureUrl;
	
	@JsonProperty("user_name")
	private String user_name;

	@JsonProperty("user_id")
	private long user_id;
	
	private List<String> tags = null;
	
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
		tags = new ArrayList<String>();
	}
	
	public Message(Long id) {
		this.id = id;
	}
	

	public long getUserId() {
		return user_id;
	}

	public void setText(String text) {
		this.text = text;
		updateTags();
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
	
	public String getUserName() {
		return user_name;
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
	
	private void updateTags() {
		String delim = "[ ,.?!;:\t\n]+";
		String[] tokens = text.split(delim);
		for (String s : tokens) {
			if (s.length()>1 && s.charAt(0) == '#') tags.add(s);
		}
	}
	
	public List<String> getTags() {
		return tags;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setUserName(String owner) {
		this.user_name = owner;
	}
	
	public String getPictureUrl() {
		return pictureUrl;
	}
	
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	
}
