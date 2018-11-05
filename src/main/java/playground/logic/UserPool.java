package playground.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import playground.logic.UserTO;

@Component
public class UserPool implements Constants {

	private List<UserTO> users = new ArrayList<>();
	private Map<String, String> emailToCode = new HashMap<>();

	public List<UserTO> getAllUsers() {
		return users;
	}

	public UserTO createUser(NewUserForm form) {
		UserTO user = new UserTO(form);
		users.add(user);
		emailToCode.put(user.getEmail(), generateRandomCode());
		return user;
	}

	public Map<String, String> getEmailToCode() {
		return emailToCode;
	}

	public UserTO getUser(String playground, String email) {
		return users.stream().filter(user -> user.getEmail().equals(email) && user.getPlayground().equals(playground))
				.findFirst().get();
	}

	public UserTO confirmUser(String playground, String email) {
		UserTO confirmedUser = getUser(playground, email);
		confirmedUser.setRole(REVIEWER);
		return confirmedUser;
	}

	public void editUser(String playground, String email, UserTO newUser) {
		getUser(playground, email).setParams(newUser);
	}

	private String generateRandomCode() {
		String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String code = "";
		for (int i = 0; i < 6; i++)
			code += text.charAt((int) (Math.random() * text.length()));
		return code;
	}
}
