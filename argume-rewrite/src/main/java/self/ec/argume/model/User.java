package self.ec.argume.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = -7212786157292329086L;

	public static enum Role {
		REGULAR, ADMIN
	}
	
	public static enum Source {
		ARGUME, FACEBOOK, TWITTER
	}
	
	@Column(name = "email", unique = true, nullable = false)
	private String email;
	
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;
	
	@Column(name = "password_salt", nullable = false)
	private String passwordSalt;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Role role = Role.REGULAR;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "source")
	private Source source;
	
	@Column(name = "id_at_source")
	private String idAtSource;
	
	@Column(name = "img_url")
	private String imgUrl;
	
	public User() {
		super(null, null);
	}
	
	public User(String email, String passwordHash, String passwordSalt) {
		this(email, passwordHash, passwordSalt, null, null, Role.REGULAR);
	}
	
	public User(String email, String passwordHash, String passwordSalt,
			String firstName, String lastName, Role role) {
		super(null, null);
		this.email = email;
		this.passwordHash = passwordHash;
		this.passwordSalt = passwordSalt;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getIdAtSource() {
		return idAtSource;
	}

	public void setIdAtSource(String idAtSource) {
		this.idAtSource = idAtSource;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public boolean isAdmin() {
		return Role.ADMIN == this.role;
	}
	
	public boolean isFromFacebook() {
		return Source.FACEBOOK == this.source;
	}
	
	public void clearPassword() {
		this.passwordHash = null;
		this.passwordSalt = null;
	}

}