package playground.layout;

import playground.logic.Constants;

public class NewUserForm implements Constants {
	private String email;
	private String username;
	private String avatar;
	private String role;
	
	public NewUserForm() { // added random default values for testing
		this.email = MANAGER_MAIL;
		this.username = "anonymous";
		this.avatar = "(-(-_(-_-)_-)-)";
		this.role = GUEST;	
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

	public void setEmail(String email) throws Exception {
		validateNull(email);
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) throws Exception {
		validateNull(username);
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) throws Exception {
		validateNull(avatar);
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) throws Exception {
		validateNull(role);
		this.role = role;
	}
	
	private void validateNull(String string) throws Exception {
		if ("null".equals(string) || string == null)
			throw new Exception("One of the paramters provided was null");
	}

	@Override
	public String toString() {
		return "NewUserForm [email=" + email + ", username=" + username + ", avatar=" + avatar + ", role=" + role + "]";
	}
}