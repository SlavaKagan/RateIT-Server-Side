package playground.logic.data;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.dal.NumberGenerator;
import playground.dal.NumberGeneratorDao;
import playground.dal.UserDao;
import playground.logic.ConfirmationException;
import playground.logic.UserEntity;
import playground.logic.UserService;

@Service
public class JpaUserService implements UserService {
	private UserDao users;
	private NumberGeneratorDao numberGenerator;

	@Value("${guest:Anonymous}")
	private String guest;

	@Value("${temporary.code:Anonymous}")
	private String temporary_code;

	@Value("${reviewer:Anonymous}")
	private String reviewer;

	@Value("${playground:Anonymous}")
	private String playground;
	
	@Value("${delim:@@}")
	private String delim;

	@Autowired
	public void setElementDao(UserDao users, NumberGeneratorDao numberGenerator) {
		this.users = users;
		this.numberGenerator = numberGenerator;
	}

	@Override
	@Transactional
	public UserEntity createUser(UserEntity userEntity) throws Exception {
		if (!this.users.existsById(userEntity.getUniqueKey())) {
			NumberGenerator temp = this.numberGenerator.save(new NumberGenerator());
			String number = "" + temp.getNextNumber();
			userEntity.setNumber(number);
			
			String email = userEntity.getUniqueKey().split(delim)[1];
			userEntity.setUniqueKey(playground + delim + email);
			
			this.numberGenerator.delete(temp);

			UserEntity saved = this.users.save(userEntity);
			return saved;
		} else {
			throw new RuntimeException("User already exists!");
		}

	}

	@Override
	@Transactional(readOnly = true)
	public UserEntity getUser(String uniqueKey) {
		Optional<UserEntity> op = this.users.findById(uniqueKey);
		if (op.isPresent()) {
			return op.get();
		} else {
			throw new RuntimeException("no user with uniqueKey: " + uniqueKey);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public UserEntity getRegisteredUser(String playground, String email) throws ConfirmationException {
		Optional<UserEntity> user = this.users.findById(playground + delim + email);
		if (!user.isPresent())
			throw new ConfirmationException("This is an unregistered account");
		else if (!user.get().getUniqueKey().split(delim)[0].equals(playground))
			throw new ConfirmationException("There's no such user in the specified playground");
		else if (user.get().getRole().equals(guest))
			throw new ConfirmationException("This is an unconfirmed account");
		else
			return user.get();
	}

	@Override
	@Transactional
	public UserEntity confirmUser(String playground, String email, String code) throws Exception {
		Optional<UserEntity> user = this.users.findById(playground + delim + email);
		if (!user.isPresent())
			throw new ConfirmationException("This is an unregistered account");
		else if (!user.get().getUniqueKey().split(delim)[0].equals(playground))
			throw new ConfirmationException("There's no such user in the specified playground");
		else if (!user.get().getRole().equals(guest))
			throw new ConfirmationException("This user is already confirmed");
		else if (!code.equals(temporary_code))
			throw new ConfirmationException("Code given is incorrect");
		else {
			user.get().setRole(reviewer);
			return user.get();
		}
	}

	@Override
	@Transactional
	public void editUser(String playground, String uniqueKey, UserEntity newUser) throws Exception {
		UserEntity existing = this.getUser(uniqueKey);

		if (newUser.getAvatar() != null && !newUser.getAvatar().equals(existing.getAvatar())) {
			existing.setAvatar(newUser.getAvatar());
		}

		if (newUser.getUserName() != null && !newUser.getUserName().equals(existing.getUserName())) {
			existing.setUserName(newUser.getUserName());
		}

		if (newUser.getRole() != null && !newUser.getRole().equals(existing.getRole())) {
			existing.setRole(newUser.getRole());
		}

		this.users.save(existing);
	}

	@Override
	@Transactional
	public void cleanup() {
		this.users.deleteAll();
	}

}
