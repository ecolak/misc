package self.ec.argume.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import self.ec.argume.util.Messages;


@Entity
@Table(name = "articles")
public class Article extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 5831489499445317577L;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "body", columnDefinition = "text")
	private String body;
	
	@Column(name = "img_url")
	private String imgUrl;
	
	@Column(name = "img2_url")
	private String img2Url;
	
	@Column(name = "category_id")
	private long categoryId;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "is_verified")
	private Boolean verified = false;
	
	@Transient
	private int votesFor;
	
	@Transient
	private int votesAgainst;
	
	@Transient
	private int votesForPct;
	
	@Transient
	private int votesAgainstPct;
	
	@Transient
	private ResultList<Argument> argumentsFor;
	
	@Transient
	private ResultList<Argument> argumentsAgainst;
	
	public Article() {
		super(null, null);
	}
	
	public Article(String title, String body, String imgUrl, String img2Url,
			long categoryId, String source, String location, boolean verified) {
		super(null, null);
		this.title = title;
		this.body = body;
		this.imgUrl = imgUrl;
		this.img2Url = img2Url;
		this.categoryId = categoryId;
		this.source = source;
		this.location = location;
		this.verified = verified;
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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getImg2Url() {
		return img2Url;
	}

	public void setImg2Url(String img2Url) {
		this.img2Url = img2Url;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
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

	public Boolean isVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public int getVotesFor() {
		return votesFor;
	}

	public void setVotesFor(int votesFor) {
		this.votesFor = votesFor;
	}

	public int getVotesAgainst() {
		return votesAgainst;
	}

	public void setVotesAgainst(int votesAgainst) {
		this.votesAgainst = votesAgainst;
	}

	public int getVotesForPct() {
		return votesForPct;
	}

	public void setVotesForPct(int votesForPct) {
		this.votesForPct = votesForPct;
	}

	public int getVotesAgainstPct() {
		return votesAgainstPct;
	}

	public void setVotesAgainstPct(int votesAgainstPct) {
		this.votesAgainstPct = votesAgainstPct;
	}

	public ResultList<Argument> getArgumentsFor() {
		return argumentsFor;
	}

	public void setArgumentsFor(ResultList<Argument> argumentsFor) {
		this.argumentsFor = argumentsFor;
	}

	public ResultList<Argument> getArgumentsAgainst() {
		return argumentsAgainst;
	}

	public void setArgumentsAgainst(ResultList<Argument> argumentsAgainst) {
		this.argumentsAgainst = argumentsAgainst;
	}

	public int getTotalVotes() {
		return votesFor + votesAgainst;
	}
	
	public String getVerifiedInTurkish() {
		return verified ? Messages.getMessage("verified.true") : Messages.getMessage("verified.false");
	}
}