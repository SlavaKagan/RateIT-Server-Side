package playground.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import playground.logic.ActivityService;
import playground.logic.ConfirmationException;
import playground.logic.ElementNotFoundException;
import playground.logic.NotFoundExceptions;

@RestController
public class ActivityWebUI {
	
	private ActivityService service;
	
	@Autowired
	public void setService(ActivityService service) {
		this.service = service;
	}
	
	@RequestMapping(
			method= RequestMethod.POST,
			path = "/playground/activities/{userPlayground}/{email}",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object getActivity(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestBody ActivityTO theActivity) throws Exception {
		validateParamsNotNull(userPlayground,email);
		switch(theActivity.getType()) {
		case "x":
			return new UserTO(new NewUserForm("rubykozel@gmail.com","ruby",":-)","Guest"));
		default:
			return new UserTO();
		}
	}
	
	@ExceptionHandler({
		ElementNotFoundException.class, 
		ConfirmationException.class
		})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException(NotFoundExceptions e) {
		String msg = e.getMessage();
		return new ErrorMessage(msg == null ? "There's no specified message for this exception" : msg);
	}
	
	private void validateParamsNotNull(String... strings) throws Exception {
		for(String string : strings) 
			if ("null".equals(string) || string==null)
				throw new Exception("One of the paramters provided was null");					
	}
}
