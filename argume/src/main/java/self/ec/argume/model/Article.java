package self.ec.argume.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


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
	
	@Column(name = "category_id")
	private long categoryId;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "pub_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date pubDate;
	
	public Article() {
		super(null, null);
	}

	public Article(String title, String body, String imgUrl,
			long categoryId, String source, String location, Date pubDate,
			int pctTrue, int pctFalse) {
		super(null, null);
		this.title = title;
		this.body = body;
		this.imgUrl = imgUrl;
		this.categoryId = categoryId;
		this.source = source;
		this.location = location;
		this.pubDate = pubDate;
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

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

}