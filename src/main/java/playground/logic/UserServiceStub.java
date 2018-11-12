package playground.logic;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class UserServiceStub implements Constants, UserService {

	private Map<String, UserEntity> users;
	
	@PostConstruct
	public void init() {
		users = new HashMap<>();
	}

	public Map<String, UserEntity> getAllUsers() {
		return users;
	}

	public void createUser(NewUserForm form) {
		UserEntity user = new UserEntity(form);
		this.users.put(user.getEmail(), user);
	}

	public UserEntity getUser(String playground, String email) throws ConfirmationException {
		UserEntity user = users.get(email);
		if(user == null)
			throw new ConfirmationException("This is an unregistered account");
		else if (!user.getPlayground().equals(playground))
			throw new ConfirmationException("There's no such user in the specified playground");		
		else if(user.getRole().equals(GUEST))
			throw new ConfirmationException("This is an unconfirmed account");
		else
			return user;
	}

	public UserEntity confirmUser(String playground, String email, String code) throws Exception {
		UserEntity confirmedUser = getUser(playground, email);
		if (code.equals(TEMPORARY_CODE) && confirmedUser.getRole().equals(GUEST))
			confirmedUser.setRole(REVIEWER);		
		else if (confirmedUser.getRole().equals(REVIEWER) || confirmedUser.getRole().equals(MANAGER))
			throw new ConfirmationException("User is already confirmed");
		else if (!code.equals(TEMPORARY_CODE))
			throw new ConfirmationException("You have entered the wrong confirmation code");
		return confirmedUser;
	}

	public void editUser(String playground, String email, UserEntity newUser) throws ConfirmationException {
		UserEntity user = getUser(playground, email);
		if(user == null)
			throw new ConfirmationException("This is an unregistered account");
		user.setParams(newUser);
	}

	private String generateRandomCode() {
		String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String code = "";
		for (int i = 0; i < 6; i++)
			code += text.charAt((int) (Math.random() * text.length()));
		return code;
	}

	@Override
	public void cleanup() {
		this.users.clear();
	}
}
