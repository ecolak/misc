package com.abe.olasihaber.model;

public class Argument {
	
	private long id;
	private long article_id;
	private String summary;
	private String body;
	private int likes;
	private int dislikes;
	
	public Argument() {}
	
	public Argument(long id, long article_id, String summary, String body,
			int likes, int dislikes) {
		this.id = id;
		this.article_id = article_id;
		this.summary = summary;
		this.body = body;
		this.likes = likes;
		this.dislikes = dislikes;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getArticle_id() {
		return article_id;
	}

	public void setArticle_id(long article_id) {
		this.article_id = article_id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
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
	
}
