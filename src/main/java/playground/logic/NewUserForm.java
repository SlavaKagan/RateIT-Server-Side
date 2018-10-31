package playground.logic;

public class NewUserForm implements Constants {
	private String email;
	private String username;
	private String avatar;
	private String role;
	
	public NewUserForm() { // added random default values for testing
		this.email = MANAGER_MAIL;
		this.username = "anonymous";
		this.avatar = "(-(-_(-_-)_-)-)";
		this.role = MANAGER_NAME;	
	}
	
	public NewUserForm(String email, String username, String avatar, String role) {
		this.email = email;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "NewUserForm [email=" + email + ", username=" + username + ", avatar=" + avatar + ", role=" + role + "]";
	}

}
