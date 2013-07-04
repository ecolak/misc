package com.abe.olasihaber.model;

public class Vote {
	
	private long id;
	private long article_id;
	private String ip;
	private int vote_type;
	
	private static long nextId = 0;
	
	public Vote(){}

	public Vote(long article_id, String ip, int vote_type) {
		this.id = nextId++;
		this.article_id = article_id;
		this.ip = ip;
		this.vote_type = vote_type;		
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getVote_type() {
		return vote_type;
	}

	public void setVote_type(int vote_type) {
		this.vote_type = vote_type;
	}
	
}
