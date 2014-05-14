package self.ec.argume.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "likes")
public class Like extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 4174652961154228927L;
	
	@Column(name = "argument_id")
	private Long argumentId;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "visitor_id")
	private String visitorId;
	
	@Column(name = "is_favorable")
	private boolean favorable;
	
	@Column(name = "ip")
	private String ip;
	
	public Like() {
		super(null, null);
	}
	
	public Like(Long argumentId, Long userId, String visitorId, boolean favorable, String ip) {
		super(null, null);
		this.argumentId = argumentId;
		this.userId = userId;
		this.visitorId = visitorId;
		this.favorable = favorable;
		this.ip = ip;
	}

	public Long getArgumentId() {
		return argumentId;
	}

	public void setArgumentId(Long argumentId) {
		this.argumentId = argumentId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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