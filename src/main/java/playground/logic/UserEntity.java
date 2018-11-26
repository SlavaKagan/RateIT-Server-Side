package playground.logic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import playground.logic.Constants;

@Entity
@Table(name="USERS")
public class UserEntity implements Constants {
	private String uniqueKey;
	private String userName;
	private String avatar;
	private String role;
	private long points;
	private String number;

	public UserEntity() {

	}

	public UserEntity(String uniqueKey, String userName, String avatar, String role) {
		this.uniqueKey = uniqueKey;
		this.userName = userName;
		this.avatar = avatar;
		this.role = role;
		setStartingPoints();
	}
	
	@Id
	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
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

	public void setStartingPoints() {
		if (this.role == MANAGER) {
			this.points = 0;
		} else if (this.role == REVIEWER) {
			this.points = 100;
		}
	}
	
	public void setPoints(long points) {
		this.points = points;
	}

	public void updatePoints(int amount) {
		this.points += amount;
	}
	
	public String getEmail() {
		return this.getUniqueKey().split("@@")[1];
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getPlayground() {
		return this.getUniqueKey().split("@@")[0];
	}
	
	private void validateNull(String string) throws Exception {
		if ("null".equals(string) || string == null)
			throw new Exception("One of the paramters provided was null");
	}
}