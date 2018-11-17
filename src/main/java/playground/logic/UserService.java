package playground.logic;

public interface UserService {
	
	public void createUser(NewUserForm form) throws Exception;
	
	public UserEntity getUser(String playground, String email) throws ConfirmationException;
	
	public UserEntity confirmUser(String playground, String email, String code) throws Exception;
	
	public void editUser(String playground, String email, UserEntity newUser) throws Exception;

	public void cleanup();

}
