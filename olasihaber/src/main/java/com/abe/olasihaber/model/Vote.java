package com.abe.olasihaber.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "votes", uniqueConstraints=@UniqueConstraint(columnNames = {"visitor_id", "article_id"}))

public class Vote implements java.io.Serializable {
	
	private static final long serialVersionUID = -3843283534105470459L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;
	
	@Column(name = "visitor_id")
	private String visitorId;
	
	@Column(name = "article_id")
	private Long articleId;
	
	@Column(name = "is_favorable")
	private boolean isFavorable;
	
	@Column(name = "ip")
	private String ip;
	
	public Vote(){}

	public Vote(Long articleId, String visitorId, boolean isFavorable, String ip) {
		this.articleId = articleId;
		this.visitorId = visitorId;
		this.isFavorable = isFavorable;
		this.ip = ip;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}

	public String getVisitorId() {
		return visitorId;
	}

	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}

	public boolean isFavorable() {
		return isFavorable;
	}

	public void setFavorable(boolean isFavorable) {
		this.isFavorable = isFavorable;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
