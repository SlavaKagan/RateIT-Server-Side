package playground.logic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name="USERS")
public class UserEntity {
	private String uniqueKey;
	private String userName;
	private String avatar;
	private String role;
	private long points;
	private String number;
	private String code;
	
	@Value("${manager:Anonymous}")
	private String manager;
	
	@Value("${reviewer:Anonymous}")
	private String reviewer;

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
		this.userName = userName;
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

	public long getPoints() {
		return points;
	}

	public void setStartingPoints() {
		if (this.role.equals(manager)) {
			this.points = 0;
		} else if (this.role.equals(reviewer)) {
			this.points = 100;
		}
	}
	
	public void setPoints(long points) {
		this.points = points;
	}

	public void updatePoints(int amount) {
		this.points += amount;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public String toString() {
		return "UserEntity [uniqueKey=" + uniqueKey + ", userName=" + userName + ", avatar=" + avatar + ", role=" + role
				+ ", points=" + points + ", number=" + number + ", code=" + code + ", manager=" + manager
				+ ", reviewer=" + reviewer + "]";
	}


}