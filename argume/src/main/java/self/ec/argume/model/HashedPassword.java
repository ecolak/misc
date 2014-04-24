package self.ec.argume.model;

public class HashedPassword {

	private String hash;
	private String salt;
	
	public HashedPassword() {}
	
	public HashedPassword(String hash, String salt) {
		this.hash = hash;
		this.salt = salt;
	}

	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
}
