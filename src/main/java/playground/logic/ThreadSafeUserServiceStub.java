package playground.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

//@Service
public class ThreadSafeUserServiceStub implements UserService {

	private Map<String, UserEntity> users;
	
	private String temporary_code;
	private String reviewer;
	private String guest;
	private String manager;
	private String delim;
	
	@PostConstruct
	public void init() {
		users = Collections.synchronizedMap(new HashMap<>());
	}
	
	@Autowired
	public void setConstants(
			@Value("${temporary.code:Anonymous}") String temporary_code,
			@Value("${reviewer:Anonymous}") String reviewer,
			@Value("${guest:Anonymous}") String guest,
			@Value("${manager:Anonymous}") String manager,
			@Value("${delim:@@}") String delim) {
		this.reviewer = reviewer;
		this.temporary_code = temporary_code;
		this.guest = guest;
		this.manager = manager;
		this.delim = delim;
		
	}

	public Map<String, UserEntity> getAllUsers() {
		return users;
	}

	public UserEntity createUser(UserEntity user) throws Exception {
		return this.users.put(user.getUniqueKey().split(delim)[1], user);
	}
	
	@Override
	public UserEntity getUser(String playground, String email) {
		return users.get(email);
	}

	public UserEntity getRegisteredUser(String playground, String email) throws ConfirmationException {
		UserEntity user = users.get(email);
		if(user == null)
			throw new ConfirmationException("This is an unregistered account");
		else if (!user.getUniqueKey().split(delim)[0].equals(playground))
			throw new ConfirmationException("There's no such user in the specified playground");		
		else if(user.getRole().equals(guest))
			throw new ConfirmationException("This is an unconfirmed account");
		else
			return user;
	}

	public UserEntity confirmUser(String playground, String email, String code) throws Exception {
		UserEntity confirmedUser = users.get(email);
		if(!confirmedUser.getUniqueKey().split(delim)[0].equals(playground))
			throw new ConfirmationException("There's no such user in the specified playground");
		else if (code.equals(temporary_code) && confirmedUser.getRole().equals(guest)){
			confirmedUser.setRole(reviewer);
			confirmedUser.setPoints(100);
		}
		else if (confirmedUser.getRole().equals(reviewer) || confirmedUser.getRole().equals(manager))
			throw new ConfirmationException("User is already confirmed");
		else if (!code.equals(temporary_code))
			throw new ConfirmationException("You have entered the wrong confirmation code");
		return confirmedUser;
	}

	public void editUser(String playground, String email, UserEntity newUser) throws Exception {
		if(newUser.getUniqueKey().split(delim)[1] == null)
			throw new Exception("Email of user can't be null");
		UserEntity user = getRegisteredUser(playground, email);
		if(user == null)
			throw new ConfirmationException("This is an unregistered account");
		this.users.remove(email);
		this.users.put(newUser.getUniqueKey().split(delim)[1], newUser);
	}

	@Override
	public void cleanup() {
		this.users.clear();
	}
}
