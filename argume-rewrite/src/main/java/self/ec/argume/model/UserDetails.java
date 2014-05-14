package self.ec.argume.model;

public class UserDetails {

	private String firstName;
	private String lastName;
	private String imgUrl;
	
	public UserDetails() {}
	
	public UserDetails(String firstName, String lastName, String imgUrl) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.imgUrl = imgUrl;
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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
}
