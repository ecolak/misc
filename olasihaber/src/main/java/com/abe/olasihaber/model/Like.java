package com.abe.olasihaber.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "likes", uniqueConstraints=@UniqueConstraint(columnNames = {"visitor_id", "argument_id"}))
public class Like implements java.io.Serializable {

	private static final long serialVersionUID = 4174652961154228927L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;
	
	@Column(name = "argument_id")
	private Long argumentId;
	
	@Column(name = "visitor_id")
	private String visitorId;
	
	@Column(name = "is_favorable")
	private boolean favorable;
	
	@Column(name = "ip")
	private String ip;
	
	public Like() {}
	
	public Like(Long argumentId, String visitorId, boolean favorable, String ip) {
		this.argumentId = argumentId;
		this.visitorId = visitorId;
		this.favorable = favorable;
		this.ip = ip;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getArgumentId() {
		return argumentId;
	}

	public void setArgumentId(Long argumentId) {
		this.argumentId = argumentId;
	}

	public String getVisitorId() {
		return visitorId;
	}

	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}

	public boolean isFavorable() {
		return favorable;
	}

	public void setFavorable(boolean favorable) {
		this.favorable = favorable;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
