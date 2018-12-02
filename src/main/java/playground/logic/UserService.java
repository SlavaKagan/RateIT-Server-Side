package playground.logic;

public interface UserService {
	
	public UserEntity createUser(UserEntity userEntity) throws Exception;
	
	public UserEntity getUser(String uniqueKey);
	
	public UserEntity getRegisteredUser(String playground, String uniqueKey) throws ConfirmationException;
	
	public UserEntity confirmUser(String playground, String uniqueKey, String code) throws Exception;
	
	public void editUser(String playground, String uniqueKey, UserEntity newUser) throws Exception;
	
	public void cleanup();
}