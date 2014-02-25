package com.sims.topaz.modele;

import com.sims.topaz.network.modele.Comment;

import android.graphics.Bitmap;

public class CommentItem {
	private long id;
	private String user;
	private String commentText;
	private long date;
	private Bitmap icon;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getCommentText() {
		return commentText;
	}
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public Bitmap getIcon() {
		return icon;
	}
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public CommentItem(long id, String user, String commentText, long date,
			Bitmap icon) {
		super();
		this.id = id;
		this.user = user;
		this.commentText = commentText;
		this.date = date;
		this.icon = icon;
	}
	
	public CommentItem(Comment comment) {
		super();
		this.id = comment.getId();
		this.user = comment.getUserName();
		this.commentText = comment.getText();
		this.date = comment.getTimestamp();
		this.icon = null;
	}
	
		
}
