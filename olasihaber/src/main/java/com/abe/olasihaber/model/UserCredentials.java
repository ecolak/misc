package com.abe.olasihaber.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_credentials")
public class UserCredentials implements java.io.Serializable {

	private static final long serialVersionUID = 4722269479058518651L;
	
	@Id
    @Column(name = "id")
	private Long id;

	@Column(name = "password", nullable = false)
	private String password;
	
	@Column(name = "salt", nullable = false)
	private String salt;
	
	public UserCredentials() {}

	public UserCredentials(Long id, String password, String salt) {
		this.id = id;
		this.password = password;
		this.salt = salt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

}
