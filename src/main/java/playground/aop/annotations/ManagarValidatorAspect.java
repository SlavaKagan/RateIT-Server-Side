package playground.aop.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import playground.logic.UserEntity;
import playground.logic.UserService;

@Component
@Aspect
public class ManagarValidatorAspect {
	
	private UserService userService;
	
	@Value("${delim:@@}")
	private String delim;
	
	@Value("${manager:Manager}")
	private String manager;
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Around("@annotation(playground.aop.annotations.ValidateManager) && args(userPlayground,email,..)")
	public Object validateManager(ProceedingJoinPoint jp, String userPlayground, String email) throws Throwable {
		try {
			UserEntity user = userService.getUser(userPlayground + delim + email);
			if(user.getRole().equals(manager))
				return jp.proceed();
			else
				throw new Exception("User is not managar!");
		} catch (Throwable t) {
			throw t;
		}
	}
}
