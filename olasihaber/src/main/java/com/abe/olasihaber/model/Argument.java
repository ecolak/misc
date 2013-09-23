package com.abe.olasihaber.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "arguments")
public class Argument implements java.io.Serializable  {
	
	private static final long serialVersionUID = -4528621710612207100L;
	
	public static enum Status {
		pending, approved
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;
	
	@Column(name = "article_id", nullable = false)
	private Long articleId;
	
	@Column(name = "summary")
	private String summary;
	
	@Column(name = "body", columnDefinition = "text")
	private String body;
	
	@Column(name = "is_affirmative")
	private boolean affirmative;
	
	@Column(name = "status")
	private Status status = Status.pending;
	
	@Transient
	private int likes;
	
	@Transient
	private int dislikes;
	
	public Argument() {}

	public Argument(Long articleId, String summary, String body, boolean affirmative, Status status) {
		this.articleId = articleId;
		this.summary = summary;
		this.body = body;
		this.affirmative = affirmative;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
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

	public boolean isAffirmative() {
		return affirmative;
	}

	public void setAffirmative(boolean affirmative) {
		this.affirmative = affirmative;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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
