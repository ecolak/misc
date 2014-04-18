package self.ec.argume.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "arguments")
public class Argument extends BaseEntity implements java.io.Serializable  {
	
	private static final long serialVersionUID = -4528621710612207100L;
	
	private static final float MAGIC_NUMBER_LAMBDA = 100f;
	private static final float MU = 0.1f;
	
	public static enum Status {
		PENDING, APPROVED
	}
	
	@Column(name = "article_id", nullable = false)
	private Long articleId;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "summary")
	private String summary;
	
	@Column(name = "body", columnDefinition = "text")
	private String body;
	
	@Column(name = "is_affirmative")
	private boolean affirmative;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status = Status.PENDING;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "login_type")
	private Login.Type loginType;
	
	@Transient
	private int likes;
	
	@Transient
	private int dislikes;
	
	@Transient
	private String fbAccessToken;
	
	public Argument() {
		super(null, null);
	}

	public Argument(Long articleId, String summary, String body, 
					boolean affirmative, Status status) {
		this(articleId, summary, body, affirmative, status, null, null);
	}
	
	public Argument(Long articleId, String summary, String body, 
					boolean affirmative, Status status, Long userId, Login.Type loginType) {
		super(null, null);
		this.articleId = articleId;
		this.summary = summary;
		this.body = body;
		this.affirmative = affirmative;
		this.status = status;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
	
	public Login.Type getLoginType() {
		return loginType;
	}

	public void setLoginType(Login.Type loginType) {
		this.loginType = loginType;
	}

	public String getFbAccessToken() {
		return fbAccessToken;
	}

	public void setFbAccessToken(String fbAccessToken) {
		this.fbAccessToken = fbAccessToken;
	}

	public static float magicScore(int likes, int dislikes) {
		return (likes - dislikes) + (2 * likes  / MAGIC_NUMBER_LAMBDA) - (dislikes * MU);
	}
	
}