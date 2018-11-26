package playground.logic.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.dal.UserDao;
import playground.logic.ConfirmationException;
import playground.logic.UserEntity;
import playground.logic.UserService;

@Service
public class JpaUserDao implements UserService {
	
	private UserDao users;

	@Autowired
	public void setElementDao(UserDao users){
		this.users = users;
	}
	
	@Override
	@Transactional
	public void createUser(UserEntity userEntity) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional(readOnly=true)
	public UserEntity getUser(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public UserEntity getRegisteredUser(String playground, String email) throws ConfirmationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public UserEntity confirmUser(String playground, String email, String code) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public void editUser(String playground, String email, UserEntity newUser) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

}
