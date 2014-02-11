package com.sims.topaz.modele;

import android.graphics.Bitmap;

public class CommentItem {
	private long id;
	private String user;
	private String commentText;
	private long date;
	private Bitmap icon;
	private float note;
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
	public float getNote() {
		return note;
	}
	public void setNote(float note) {
		this.note = note;
	}
	public CommentItem(long id, String user, String commentText, long date,
			Bitmap icon, float note) {
		super();
		this.id = id;
		this.user = user;
		this.commentText = commentText;
		this.date = date;
		this.icon = icon;
		this.note = note;
	}
	
		
}
