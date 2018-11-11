package playground.logic;

public class UserTO implements Constants {
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
		this.playground = PLAYGROUND;
		this.userName = form.getUsername();
		this.avatar = form.getAvatar();
		this.role = form.getRole();
		setPoints();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) throws Exception {
		validateNull(email);
		this.email = email;
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) throws Exception {
		validateNull(playground);
		this.playground = playground;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) throws Exception {
		validateNull(userName);
		this.userName = userName;
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

	public long getPoints() {
		return points;
	}

	public void setPoints() {
		if (this.role == MANAGER) {
			this.points = 0;
		} else if (this.role == REVIEWER) {
			this.points = 100;
		}
	}

	public void updatePoints(int amount) {
		this.points += amount;
	}
	
	private void validateNull(String string) throws Exception {
		if ("null".equals(string) || string == null)
			throw new Exception("One of the paramters provided was null");
	}

	public void setParams(UserTO newUser) {
		this.email = newUser.email;
		this.playground = newUser.playground;
		this.userName = newUser.userName;
		this.avatar = newUser.avatar;
		this.role = newUser.role;
		this.points = newUser.points;

	}

}
