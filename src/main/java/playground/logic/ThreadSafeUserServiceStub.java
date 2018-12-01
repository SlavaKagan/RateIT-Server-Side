package playground.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ThreadSafeUserServiceStub implements UserService {

	private Map<String, UserEntity> users;
	
	@Value("${temporary.code:Anonymous}")
	private String temporary_code;
	
	@Value("${reviewer:Anonymous}")
	private String reviewer;
	
	@Value("${guest:Anonymous}")
	private String guest;
	
	@Value("${manager:Anonymous}")
	private String manager;
	
	@PostConstruct
	public void init() {
		users = Collections.synchronizedMap(new HashMap<>());
	}

	public Map<String, UserEntity> getAllUsers() {
		return users;
	}

	public void createUser(UserEntity user) throws Exception {
		this.users.put(user.getUniqueKey().split("@@")[1], user);
	}
	
	@Override
	public UserEntity getUser(String email) {
		return users.get(email);
	}

	public UserEntity getRegisteredUser(String playground, String email) throws ConfirmationException {
		UserEntity user = users.get(email);
		if(user == null)
			throw new ConfirmationException("This is an unregistered account");
		else if (!user.getUniqueKey().split("@@")[0].equals(playground))
			throw new ConfirmationException("There's no such user in the specified playground");		
		else if(user.getRole().equals(guest))
			throw new ConfirmationException("This is an unconfirmed account");
		else
			return user;
	}

	public UserEntity confirmUser(String playground, String email, String code) throws Exception {
		UserEntity confirmedUser = users.get(email);
		if(!confirmedUser.getUniqueKey().split("@@")[0].equals(playground))
			throw new ConfirmationException("There's no such user in the specified playground");
		else if (code.equals(temporary_code) && confirmedUser.getRole().equals(guest))
			confirmedUser.setRole(reviewer);		
		else if (confirmedUser.getRole().equals(reviewer) || confirmedUser.getRole().equals(manager))
			throw new ConfirmationException("User is already confirmed");
		else if (!code.equals(temporary_code))
			throw new ConfirmationException("You have entered the wrong confirmation code");
		return confirmedUser;
	}

	public void editUser(String playground, String email, UserEntity newUser) throws Exception {
		if(newUser.getUniqueKey().split("@@")[1] == null)
			throw new Exception("Email of user can't be null");
		UserEntity user = getRegisteredUser(playground, email);
		if(user == null)
			throw new ConfirmationException("This is an unregistered account");
		this.users.remove(email);
		this.users.put(newUser.getUniqueKey().split("@@")[1], newUser);
	}

//	private String generateRandomCode() {
//		String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//		String code = "";
//		for (int i = 0; i < 6; i++)
//			code += text.charAt((int) (Math.random() * text.length()));
//		return code;
//	}

	@Override
	public void cleanup() {
		this.users.clear();
	}
}
