package playground.aop.annotations;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import playground.dal.UserDao;
import playground.logic.ConfirmationException;
import playground.logic.UserEntity;

@Component
@Aspect
public class UserValidatorAspect {

	private UserDao users;

	@Value("${delim:@@}")
	private String delim;
	
	@Value("${guest:Guest}")
	private String guest;
	
	@Autowired
	public void setUserService(UserDao users) {
		this.users = users;
	}

	@Around("@annotation(playground.aop.annotations.ValidateUser) && args(playground,email,..)")
	public Object validateUser(ProceedingJoinPoint jp, String playground, String email) throws Throwable {
		try {
			if (users.findById(playground + delim + email).isPresent())
				return jp.proceed();
			else
				throw new Exception("User doesn't exist in database!");
		} catch (Throwable t) {
			throw t;
		}
	}
	
	@Around("@annotation(playground.aop.annotations.ValidateUser) && args(playground,email,code)")
	public Object confirmUser(ProceedingJoinPoint jp, String playground, String email, String code) throws Throwable {
		Optional<UserEntity> user = this.users.findById(playground + delim + email);
		if (!user.isPresent())
			throw new ConfirmationException("This is an unregistered account");
		else if (!user.get().getRole().equals(guest))
			throw new ConfirmationException("This user is already confirmed");
		else if (!code.equals(user.get().getCode()))
			throw new ConfirmationException("Code given is incorrect");
		else {
			return jp.proceed();
		}
	}
}