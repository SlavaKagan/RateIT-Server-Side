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

import playground.aop.annotations.ValidateNull;
import playground.logic.ConfirmationException;
import playground.logic.ElementNotFoundException;
import playground.logic.NotFoundExceptions;
import playground.logic.UserService;

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
public class UserWebUI {
	private UserService userservice;
	
	@Value("${playground:default}")
	private String playground;
	
	@Autowired
	public void setService(UserService userservice) {
		this.userservice = userservice;
	}
	
	@RequestMapping(
			method = RequestMethod.POST, 
			path = "/playground/users", 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserTO createUser(@RequestBody NewUserForm userForm) throws Exception {
		UserTO user = new UserTO(userForm);
		user.setPlayground(playground);
		userservice.createUser(user.toEntity());
		return user;
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			path = "/playground/users/confirm/{playground}/{email}/{code}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO confirmUser(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email,
			@PathVariable("code") String code) throws Exception {
		return new UserTO(userservice.confirmUser(playground, email, code));
	}
	
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/playground/users/login/{playground}/{email}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO getUser(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email) throws Exception {
		return new UserTO(userservice.getRegisteredUser(playground, email));	
	}
	
	@RequestMapping(
			method = RequestMethod.PUT, 
			path = "/playground/users/{playground}/{email}", 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email, 
			@RequestBody UserTO newUser) throws Exception {
		userservice.editUser(playground, email, newUser.toEntity());
	}
	
	@ExceptionHandler({
		ElementNotFoundException.class, 
		ConfirmationException.class})
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException(NotFoundExceptions e) {
		String msg = e.getMessage();
		return new ErrorMessage(msg == null ? "There's no specified message for this exception" : msg);
	}
}
