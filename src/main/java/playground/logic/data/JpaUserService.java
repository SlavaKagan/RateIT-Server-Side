package playground.logic.data;

import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.annotations.ValidateNull;
import playground.aop.annotations.ValidateUser;
import playground.aop.logger.Logger;
import playground.aop.logger.PlaygroundPerformance;
import playground.dal.UserDao;
import playground.logic.ConfirmationException;
import playground.logic.UserEntity;
import playground.logic.UserService;

@Service
public class JpaUserService implements UserService {
	private UserDao users;
	private String guest;
	private String reviewer;
	private String playground;
	private String delim;
	
	@Autowired
	public void setElementDao(
			UserDao users, 
			@Value("${guest:Anonymous}") String guest,
			@Value("${reviewer:Anonymous}") String reviewer,
			@Value("${playground:Anonymous}") String playground,
			@Value("${delim:@@}") String delim) {
		this.users = users;
		this.guest = guest;
		this.reviewer = reviewer;
		this.playground = playground;
		this.delim = delim;
	}

	@Override
	@Transactional
	@ValidateNull
	@Logger
	@PlaygroundPerformance
	public UserEntity createUser(UserEntity userEntity) throws Exception {
		if (!this.users.existsById(userEntity.getUniqueKey())) {
			userEntity.setCode(this.generateCode());
			String email = userEntity.getUniqueKey().split(delim)[1];
			if (isValidEmailAddress(email))
				sendEmail(email,userEntity.getCode());
			
			userEntity.setUniqueKey(playground + delim + email);
			return this.users.save(userEntity);
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
		this.users.save(user);
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
		
		if(		  !newUser.getRole().equals(existing.getRole())
				|| newUser.getPoints() != existing.getPoints())
			throw new RuntimeException("you are trying to edit read-only attributes");

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
	
	/*Sending mail to the user*/
	private void sendEmail(String toEmail,String code) throws Exception {
		try {
			final String fromEmail = "2019A.Kagan@gmail.com";
			final String password = "sryy2018";
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromEmail, password);
				}
			};

			Session session = Session.getInstance(props, auth);
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject("RateIt Confirmation");
			message.setText("We just want to confirm you're you.\nHere is your code "+code);
			Transport.send(message);
		} catch (Exception ex) {
			throw new Exception();
		}
	}
	
	/*Check the validation of the email*/
	private boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}
}