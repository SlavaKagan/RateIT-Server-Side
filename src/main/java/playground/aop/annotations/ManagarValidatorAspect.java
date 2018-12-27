package playground.aop.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import playground.logic.UserService;

@Component
@Aspect
public class ManagarValidatorAspect {
	private UserService userService;	
	private String manager;

	@Autowired
	public void setUserService(
			UserService userService,
			@Value("${manager:Manager}") String manager) {
		this.userService = userService;
		this.manager = manager;
	}

	@Around("@annotation(playground.aop.annotations.ValidateManager) && args(userPlayground,email,..)")
	public Object validateManager(ProceedingJoinPoint jp, String userPlayground, String email) throws Throwable {
		try {
			if (userService.getUser(userPlayground, email).getRole().equals(manager))
				return jp.proceed();
			throw new Exception("User is not managar!");
		} catch (Throwable t) {
			throw t;
		}
	}
}
