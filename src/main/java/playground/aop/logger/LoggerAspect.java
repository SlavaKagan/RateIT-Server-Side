package playground.aop.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
//import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import playground.logic.ActivityService;
import playground.logic.ElementService;
import playground.logic.UserService;

@Component
@Aspect
public class LoggerAspect {
	private UserService users;
	private ActivityService activities;
	private ElementService elements;
	
	private Log log = LogFactory.getLog(LoggerAspect.class);
	
	@Autowired
	public void setUsers(UserService users) {
		this.users = users;
	}
	
	@Before("@annotation(playground.aop.logger.MyLog)")
	public void log (JoinPoint jp) {
		String targetClassName = jp.getTarget().getClass().getSimpleName();
		String methodName = jp.getSignature().getName();
		
		log.trace("**************** " + targetClassName + "." + methodName + "()");
//		if (this.log.isInfoEnabled()) {
//			String argumentsData = message.toString();
//			this.log.info("JpaMessageService.createMessage(" + argumentsData +")");
//		}
	}
	
	@Around("@annotation(playground.aop.logger.MyLog)")
	public Object log (ProceedingJoinPoint jp) throws Throwable {
		String targetClassName = jp.getTarget().getClass().getSimpleName();
		String methodName = jp.getSignature().getName();
		String output = "**************** " + targetClassName + "." + methodName + "()";
		log.info(output + " - begin");
		
		try {
			Object rv = jp.proceed();
			log.info(output + " - ended successfully");
			return rv;
		} catch (Throwable e) {
			log.info(output + " - ended with errors: " + e.getClass().getName());
			
			throw e;
		}
	}
	
//	@Around("@annotation(playground.aop.logger.MyLog) && args (name,..)")
	/*
	public Object logWithArgsAnalysis (ProceedingJoinPoint jp, String name) throws Throwable {
		// gateway validation:
//		if (name == null || "null".equals(name)) {
//			throw new RuntimeException("aspect blocked this method");
//		}
		// end of gateway 
		
		// logger enhancement
		String detailedDate = "no available data";
		try {
			detailedDate = this.users.getCustomMessage(name).toString();
		} catch (Exception e) {
			// do nothing
		}
		// logger enhancement
		System.err.println("****** with name of: " + name + " for entity: " + detailedDate);

		return jp.proceed();
	}	*/
}
