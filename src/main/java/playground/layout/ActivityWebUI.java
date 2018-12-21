package playground.layout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
public class ActivityWebUI {
	
	private ActivityService service;
	
	@Value("${playground:default}")
	private String playground;
	
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
		theActivity.setPlayerEmail(email);
		theActivity.setPlayerPlayground(userPlayground);
		theActivity.setPlayground(playground);
		return new ActivityTO(this.service.createActivity(userPlayground, email, theActivity.toEntity()));
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
}
