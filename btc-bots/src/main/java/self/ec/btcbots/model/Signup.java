package self.ec.btcbots.model;

public class Signup {

	private String email;
	private String password;
	private String passwordConfirmation;
	
	public Signup() {}
	
	public Signup(String email, String password, String passwordConfirmation) {
		this.email = email;
		this.password = password;
		this.passwordConfirmation = passwordConfirmation;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	};
	
}