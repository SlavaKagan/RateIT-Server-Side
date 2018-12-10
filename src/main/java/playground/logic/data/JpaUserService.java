package playground.logic.data;

import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.annotations.ValidateNull;
import playground.aop.logger.MyLog;
import playground.aop.logger.PlaygroundPerformance;
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
	@ValidateNull
	@MyLog
	@PlaygroundPerformance
	public UserEntity createUser(UserEntity userEntity) throws Exception {
		if (!this.users.existsById(userEntity.getUniqueKey())) {
			NumberGenerator temp = this.numberGenerator.save(new NumberGenerator());
			String number = "" + temp.getNextNumber();
			userEntity.setNumber(number);
			if (userEntity.getCode() == null) {
				userEntity.setCode(this.generateCode());
			}
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
	@MyLog
	@PlaygroundPerformance
	public UserEntity getUser(String uniqueKey) {
		Optional<UserEntity> op = this.users.findById(uniqueKey);
		if (op.isPresent()) {
			return op.get();
		} else {
			throw new RuntimeException("no user with uniqueKey: " + uniqueKey);
		}
	}

	
	// Need to think how to implement the logic in the annotation
	@Override
	@Transactional(readOnly = true)
	@MyLog
	@PlaygroundPerformance
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
	
	// Need to think how to implement the logic in the annotation
	@Override
	@Transactional
	@MyLog
	@PlaygroundPerformance
	public UserEntity confirmUser(String playground, String email, String code) throws Exception {
		Optional<UserEntity> user = this.users.findById(playground + delim + email);
		if (!user.isPresent())
			throw new ConfirmationException("This is an unregistered account");
		else if (!user.get().getUniqueKey().split(delim)[0].equals(playground))
			throw new ConfirmationException("There's no such user in the specified playground");
		else if (!user.get().getRole().equals(guest))
			throw new ConfirmationException("This user is already confirmed");
		else if (!code.equals(user.get().getCode()))
			throw new ConfirmationException("Code given is incorrect");
		else {
			user.get().setRole(reviewer);
			user.get().setCode(null); // Means user is registered
			return user.get();
		}
	}

	@Override
	@Transactional
	@ValidateNull
	@MyLog
	@PlaygroundPerformance
	public void editUser(String playground, String email, UserEntity newUser) throws Exception {
		UserEntity existing = this.getUser(playground + delim + email);

		if (!newUser.getAvatar().equals(existing.getAvatar())) {
			existing.setAvatar(newUser.getAvatar());
		}

		if (!newUser.getUserName().equals(existing.getUserName())) {
			existing.setUserName(newUser.getUserName());
		}

		if (!newUser.getRole().equals(existing.getRole())) {
			existing.setRole(newUser.getRole());
		}

		this.users.save(existing);
	}

	@Override
	@Transactional
	@MyLog
	@PlaygroundPerformance
	public void cleanup() {
		this.users.deleteAll();
	}
	
	private String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder rndStrBuild = new StringBuilder();
        Random rnd = new Random();
        while (rndStrBuild.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * chars.length());
            rndStrBuild.append(chars.charAt(index));
        }
        String rndString = rndStrBuild.toString();
        return rndString;

    }

}
