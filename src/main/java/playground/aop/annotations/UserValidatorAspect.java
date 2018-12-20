package playground.aop.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import playground.dal.UserDao;

@Component
@Aspect
public class UserValidatorAspect {
	
	private UserDao user;
	
	@Value("${delim:@@}")
	private String delim;
	
	@Autowired
	public void setUserService(UserDao user) {
		this.user = user;
	}
	
	@Around("@annotation(playground.aop.annotations.ValidateUser) && args(userPlayground,email,..)")
	public Object validateUser(ProceedingJoinPoint jp, String userPlayground, String email) throws Throwable {
		try {
			if(user.findById(userPlayground + delim + email).equals(null))
				return jp.proceed();
			else
				throw new Exception("User doesn't exist in database!");
		} catch (Throwable t) {
			throw t;
		}
	}
}