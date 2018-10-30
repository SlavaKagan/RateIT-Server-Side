package playground.logic;

public class UserTO {
	private String email;
	private String playground;
	private String userName;
	private String avatar;
	private String role;
	private long points;
	
	
	public UserTO() {
		
	}
	
	public UserTO(NewUserForm form) {
		this.email = form.getEmail();
		this.playground = Constants.PLAYGROUND;
		this.userName = form.getUsername();
		this.avatar = form.getAvatar();
		this.role = form.getRole();
		setPoints();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public long getPoints() {
		return points;
	}

	public void setPoints() {
		if (this.role == Constants.MANAGER) {
			this.points = 0;
		} else if (this.role == Constants.REVIEWER){
			this.points = 100;
		}
	}
	
}
