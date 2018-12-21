package playground.aop.annotations;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import playground.logic.ActivityEntity;
import playground.logic.ElementEntity;
import playground.logic.UserEntity;

@Component
@Aspect
public class NullArgumentsValidatorAspect {

	private static final String EE = "ElementEntity";
	private static final String AE = "ActivityEntity";
	private static final String UE = "UserEntity";
	private static final RuntimeException EXCEPTION = new RuntimeException("One of the parameters provided was null");

	@Around("@annotation(playground.aop.annotations.ValidateNull)")
	public Object validateNulls(ProceedingJoinPoint jp) throws Throwable {
		Object[] args = jp.getArgs();
		String[] classNames = Stream
				.of(((CodeSignature) jp.getSignature()).getParameterTypes())
				.map(cls -> cls.getSimpleName())
				.collect(Collectors.toList())
				.toArray(new String[0]);

		for (int i = 0; i < args.length; i++) {
			if (classNames[i].equals(EE))
				checkElement(args[i]);
			else if (classNames[i].equals(AE))
				checkActivity(args[i]);
			else if (classNames[i].equals(UE))
				checkUser(args[i]);
			else if ("null".equals(args[i]) || args[i] == null)
				throw EXCEPTION;
		}

		return jp.proceed();
	}

	public void checkElement(Object arg) {
		ElementEntity ee = (ElementEntity) arg;
		String[] keyParts = ee.getUniqueKey().split("@@");

		if ("null".equals(ee.getName()) 
				|| ee.getName() == null 
				|| "null".equals(ee.getType()) 
				|| ee.getType() == null
				|| "null".equals(keyParts[0]) 
				|| keyParts[0] == null 
				|| "null".equals(keyParts[1])
				|| keyParts[1] == null) {
			throw EXCEPTION;
		}
	}

	public void checkActivity(Object arg) {
		ActivityEntity ae = (ActivityEntity) arg;
		String[] keyParts = ae.getUniqueKey().split("@@");
		
		if ("null".equals(ae.getElementId()) 
				|| ae.getElementId() == null 
				|| "null".equals(ae.getElementPlayground())
				|| ae.getElementPlayground() == null 
				|| "null".equals(ae.getPlayerEmail())
				|| ae.getPlayerEmail() == null 
				|| "null".equals(ae.getPlayerPlayground())
				|| ae.getPlayerPlayground() == null 
				|| "null".equals(ae.getType()) 
				|| ae.getType() == null
				|| "null".equals(keyParts[0]) 
				|| keyParts[0] == null 
				|| "null".equals(keyParts[1])
				|| keyParts[1] == null) {
			throw EXCEPTION;
		}
	}

	public void checkUser(Object arg) {
		UserEntity ue = (UserEntity) arg;
		String[] keyParts = ue.getUniqueKey().split("@@");
		
		if ("null".equals(keyParts[0]) 
				|| keyParts[0] == null 
				|| "null".equals(keyParts[1]) 
				|| keyParts[1] == null
				|| "null".equals(ue.getUserName()) 
				|| ue.getUserName() == null 
				|| "null".equals(ue.getAvatar())
				|| ue.getAvatar() == null 
				|| "null".equals(ue.getRole()) 
				|| ue.getRole() == null) {
			throw EXCEPTION;
		}
	}
}
