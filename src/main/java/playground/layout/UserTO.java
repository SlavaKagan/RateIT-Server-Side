package playground.layout;

import playground.logic.UserEntity;

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
		this.userName = form.getUsername();
		this.avatar = form.getAvatar();
		this.role = form.getRole();
	}

	public UserTO(UserEntity user) {
		this();
		if (user != null) {
			String[] temp = user.getUniqueKey().split("@@");
			this.playground = temp[0];
			this.email = temp[1];
			this.userName = user.getUserName();
			this.avatar = user.getAvatar();
			this.role = user.getRole();
			this.points = user.getPoints();
		}
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

	public void setPoints(long points) {
		this.points = points;
	}

	public void updatePoints(int amount) {
		this.points += amount;
	}

	public UserEntity toEntity() throws Exception {
		UserEntity rv = new UserEntity();
		rv.setUserName(this.userName);
		rv.setAvatar(this.avatar);
		rv.setUniqueKey(this.playground + "@@" + this.email);
		rv.setPoints(this.points);
		rv.setRole(this.role);
		return rv;
	}

}
