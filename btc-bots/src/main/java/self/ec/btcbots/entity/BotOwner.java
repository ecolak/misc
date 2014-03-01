package self.ec.btcbots.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users_to_bots")
public class BotOwner {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@Column(name = "job_key", nullable = false)
	private String jobKey;
	
	@Column(name = "date_modified")
	private Long dateModified;

	public BotOwner() {}
	
	public BotOwner(Long userId, String jobKey) {
		this.userId = userId;
		this.jobKey = jobKey;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getJobKey() {
		return jobKey;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	public Long getDateModified() {
		return dateModified;
	}

	public void setDateModified(Long dateModified) {
		this.dateModified = dateModified;
	}
	
}