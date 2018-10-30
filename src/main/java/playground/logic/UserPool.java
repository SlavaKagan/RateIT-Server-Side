package playground.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import playground.logic.UserTO;

@Component
public class UserPool {

	private List<UserTO> users = new ArrayList<>(Arrays.asList(new UserTO(), new UserTO(), new UserTO()));

	public List<UserTO> getAllUsers() {
		return users;
	}

	public UserTO createUser(NewUserForm form) {
		UserTO user = new UserTO(form);
		users.add(user);
		return user;
	}

	public UserTO getUser(String playground, String email) {
		return users.stream().filter(user -> user.getEmail().equals(email) && user.getPlayground().equals(playground))
				.findFirst().get();
	}
}
