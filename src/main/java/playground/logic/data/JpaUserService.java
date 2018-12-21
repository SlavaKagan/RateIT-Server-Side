package playground.logic.data;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.annotations.ValidateNull;
import playground.aop.annotations.ValidateUser;
import playground.aop.logger.Logger;
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
	@Logger
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
	@Logger
	@PlaygroundPerformance
	@ValidateUser
	public UserEntity getUser(String playground, String email) {
		return this.users.findById(playground + delim + email).get();
	}

	@Override
	@ValidateUser
	@Transactional(readOnly = true)
	@Logger
	@PlaygroundPerformance
	public UserEntity getRegisteredUser(String playground, String email) throws ConfirmationException {
		UserEntity user = this.users.findById(playground + delim + email).get();
		if (user.getRole().equals(guest))
			throw new ConfirmationException("This is an unconfirmed account");
		return user;
	}
	
	@Override
	@Transactional
	@Logger
	@ValidateUser
	@PlaygroundPerformance
	public UserEntity confirmUser(String playground, String email, String code) throws Exception {
		UserEntity user = this.users.findById(playground + delim + email).get();
		user.setRole(reviewer);
		user.setPoints(100);
		user.setCode(null); // Means user is registered
		return user;
	}

	@Override
	@Transactional
	@ValidateNull
	@Logger
	@PlaygroundPerformance
	public void editUser(String playground, String email, UserEntity newUser) throws Exception {
		UserEntity existing = this.getUser(playground, email);

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
	@Logger
	@PlaygroundPerformance
	public void cleanup() {
		this.users.deleteAll();
	}
	
	private String generateCode() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
         return IntStream
        		 .range(0, 10)
        		 .boxed()
        		 .collect(Collectors.toList())
        		 .stream()
        		 .map(i -> "" + chars.charAt((int)(Math.random()*chars.length())))
        		 .collect(Collectors.joining(""));
    }

}
