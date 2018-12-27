package playground.layout;

public class NewUserForm {	
	private String email;
	private String username;
	private String avatar;
	private String role;
	
	public NewUserForm() { 

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
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) throws Exception {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) throws Exception {
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) throws Exception {
		this.role = role;
	}

	@Override
	public String toString() {
		return "NewUserForm [email=" + email + ", username=" + username + ", avatar=" + avatar + ", role=" + role + "]";
	}
}