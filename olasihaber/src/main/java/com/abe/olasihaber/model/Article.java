package com.abe.olasihaber.model;

public class Article {

	private long id;
	private String title;
	private String body;
	private String img_url;
	private long category_id;	
	private String source;
	private String location;
	private String pub_date;
	private int pct_true;
	private int pct_false;
	
	public Article() {}

	public Article(long id, String title, String body, String img_url, long category_id,
			String source, String location, String pub_date, int pct_true,
			int pct_false) {
		this.id = id;
		this.title = title;
		this.body = body;
		this.img_url = img_url;
		this.category_id = category_id;
		this.source = source;
		this.location = location;
		this.pub_date = pub_date;
		this.pct_true = pct_true;
		this.pct_false = pct_false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public long getCategory_id() {
		return category_id;
	}

	public void setCategory_id(long category_id) {
		this.category_id = category_id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPub_date() {
		return pub_date;
	}

	public void setPub_date(String pub_date) {
		this.pub_date = pub_date;
	}

	public int getPct_true() {
		return pct_true;
	}

	public void setPct_true(int pct_true) {
		this.pct_true = pct_true;
	}

	public int getPct_false() {
		return pct_false;
	}

	public void setPct_false(int pct_false) {
		this.pct_false = pct_false;
	}
	
}
