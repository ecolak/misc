package self.ec.argume.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "votes")

public class Vote extends BaseEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = -3843283534105470459L;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "visitor_id")
	private String visitorId;
	
	@Column(name = "article_id")
	private Long articleId;
	
	@Column(name = "is_favorable")
	private boolean favorable;
	
	@Column(name = "ip")
	private String ip;
	
	public Vote(){
		super(null, null);
	}

	public Vote(Long articleId, Long userId, String visitorId, boolean favorable, String ip) {
		super(null, null);
		this.articleId = articleId;
		this.userId = userId;
		this.visitorId = visitorId;
		this.favorable = favorable;
		this.ip = ip;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
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